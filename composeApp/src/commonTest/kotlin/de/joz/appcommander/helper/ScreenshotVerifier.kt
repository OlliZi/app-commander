package de.joz.appcommander.helper

import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Paint
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Arrays
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ScreenshotVerifier<T>(
	private val testClass: Class<T>,
	private val storeDirectory: File = File("./build/reports/tests/screenshots/"),
	private val goldenImageDirectory: File = File("./src/commonTest/kotlin/"),
) {
	init {
		storeDirectory.mkdirs()
	}

	fun verifyScreenshot(
		source: ComposeUiTest,
		screenshotName: String,
	) {
		val screenshotResult =
			takeScreenshot(
				source = source,
				screenshotName = screenshotName,
			)

		when (screenshotResult) {
			is ScreenshotResult.Success ->
				verifyAgainstGoldenImage(
					screenshotFile = screenshotResult.screenshot,
				)

			is ScreenshotResult.Failure -> {
				throw screenshotResult.error
			}
		}
	}

	private fun takeScreenshot(
		source: ComposeUiTest,
		screenshotName: String,
	): ScreenshotResult =
		runCatching {
			val screenshot = source.onNode(isRoot()).captureToImage()
			val pngByteArray = convertToPng(screenshot.asSkiaBitmap())

			if (pngByteArray == null || pngByteArray.isEmpty()) {
				throw Exception("Screenshot is empty.")
			}

			val file = File(storeDirectory, "$screenshotName.png")
			file.writeBytes(pngByteArray)
			ScreenshotResult.Success(screenshot = file)
		}.getOrElse { throwable ->
			ScreenshotResult.Failure(error = throwable)
		}

	private fun verifyAgainstGoldenImage(screenshotFile: File) {
		val goldenImage =
			readGoldenImageFromSrcDir(
				screenshotFileName = screenshotFile.name,
			)

		val compareResult =
			if (goldenImage.exists()) {
				Arrays.compare(screenshotFile.readBytes(), goldenImage.readBytes())
			} else {
				IMAGE_DOES_NOT_EXIST
			}

		if (compareResult == IMAGE_DOES_NOT_EXIST) {
			Files.copy(screenshotFile.toPath(), goldenImage.toPath(), StandardCopyOption.REPLACE_EXISTING)
		} else if (compareResult != IDENTICAL_IMAGES) {
			createDifferenceScreenshot(goldenImage, screenshotFile)
			val currentScreenshot =
				File(goldenImage.parentFile, "${goldenImage.nameWithoutExtension}_current.png")
			Files.copy(screenshotFile.toPath(), currentScreenshot.toPath(), StandardCopyOption.REPLACE_EXISTING)
		}

		val failMessage =
			when (compareResult) {
				IDENTICAL_IMAGES -> "WILL NOT PRINT IN TEST RESULT."
				IMAGE_DOES_NOT_EXIST -> "Hint: Golden image does not exist. Copied screenshot for you:)."
				else -> "Fail: Screenshots are not identical ($compareResult). Created screenshot diff in directory."
			}

		assertEquals(
			IDENTICAL_IMAGES,
			compareResult,
			"$failMessage\n" +
				"Current: ${screenshotFile.absolutePath}\n" +
				"Golden: ${goldenImage.absolutePath}\n" +
				"Verify your screenshots in your VCS.",
		)
	}

	private fun createDifferenceScreenshot(
		goldenImage: File,
		screenshotFile: File,
	) {
		val image1Bitmap = createBitmapFromScreenshot(screenshotFile = goldenImage)
		val image2Bitmap = createBitmapFromScreenshot(screenshotFile = screenshotFile)

		if (image1Bitmap.width != image2Bitmap.width || image1Bitmap.height != image2Bitmap.height) {
			throw IllegalStateException("Images are not the same size.")
		}

		val destination = Bitmap()
		destination.allocPixels(image1Bitmap.imageInfo)
		val canvas = Canvas(destination)
		val paint =
			Paint().apply {
				color = 0xFFFF0000.toInt()
			}
		val normalPaint = Paint()
		for (x in 0 until image1Bitmap.width) {
			for (y in 0 until image1Bitmap.height) {
				val pixelColor1 = image1Bitmap.getColor(x = x, y = y)
				val pixelColor2 = image2Bitmap.getColor(x = x, y = y)
				canvas.drawPoint(
					x.toFloat(),
					y.toFloat(),
					if (pixelColor1 == pixelColor2) normalPaint.apply { color = pixelColor1 } else paint,
				)
			}
		}

		val diffFile = File(goldenImage.parentFile, "${goldenImage.nameWithoutExtension}_diff.png")
		diffFile.writeBytes(convertToPng(destination)!!)
	}

	private fun readGoldenImageFromSrcDir(screenshotFileName: String): File {
		val sourceDirectory =
			testClass.name
				.split(".") // split class name into parts
				.dropLast(1) // remove class name
				.joinToString("/") // convert to directory path

		val parentScreenshotDir =
			File(goldenImageDirectory.absolutePath.plus("/$sourceDirectory/screenshots/"))
		parentScreenshotDir.mkdir()

		return File(parentScreenshotDir, screenshotFileName)
	}

	private fun createBitmapFromScreenshot(screenshotFile: File): Bitmap {
		val imageBitmap = Bitmap()
		val image = Image.Companion.makeFromEncoded(screenshotFile.readBytes())

		imageBitmap.allocPixels(image.imageInfo)
		image.readPixels(imageBitmap)

		return imageBitmap
	}

	private fun convertToPng(bitmap: Bitmap): ByteArray? =
		Image
			.makeFromBitmap(bitmap)
			.encodeToData(EncodedImageFormat.PNG, IMAGE_QUALITY)
			?.bytes

	private sealed interface ScreenshotResult {
		data class Success(
			val screenshot: File,
		) : ScreenshotResult

		data class Failure(
			val error: Throwable,
		) : ScreenshotResult
	}

	companion object Companion {
		private const val IMAGE_QUALITY = 100
		private const val IDENTICAL_IMAGES = 0
		private const val IMAGE_DOES_NOT_EXIST = -1
	}
}
