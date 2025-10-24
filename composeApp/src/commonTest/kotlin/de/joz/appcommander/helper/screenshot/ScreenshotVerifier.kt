package de.joz.appcommander.helper.screenshot

import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
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
	private val writeScreenshotToSrcDirectoryWhenFailed: Boolean = true,
) {
	init {
		storeDirectory.mkdirs()
	}

	fun verifyScreenshot(
		source: ComposeUiTest,
		screenshotName: String,
	) {
		val screenshotResult =
			screenshot(
				source = source,
				screenshotName = screenshotName,
			)
		when (screenshotResult) {
			is ScreenshotResult.Success ->
				innerVerify(
					screenshotFile = screenshotResult.screenshot,
				)

			is ScreenshotResult.Failure -> throw screenshotResult.error
		}
	}

	private fun screenshot(
		source: ComposeUiTest,
		screenshotName: String,
	): ScreenshotResult =
		runCatching {
			val image = source.onNode(isRoot()).captureToImage()
			val bytearray =
				Image.makeFromBitmap(image.asSkiaBitmap()).encodeToData(EncodedImageFormat.PNG, IMAGE_QUALITY)?.bytes

			if (bytearray == null || bytearray.isEmpty()) {
				throw Exception("Screenshot is empty.")
			}

			val file = File(storeDirectory, "$screenshotName.png")
			file.writeBytes(bytearray)
			ScreenshotResult.Success(screenshot = file)
		}.getOrElse { throwable ->
			ScreenshotResult.Failure(error = throwable)
		}

	private fun innerVerify(screenshotFile: File) {
		val goldenImage =
			readGoldenImageFromSrcDir(
				screenshotFileName = screenshotFile.name,
			)

		val compareResult =
			if (goldenImage.exists()) {
				Arrays.compare(screenshotFile.readBytes(), goldenImage.readBytes())
			} else {
				IMAGES_DOES_NOT_EXIST
			}

		if (compareResult != IDENTICAL_IMAGES && writeScreenshotToSrcDirectoryWhenFailed) {
			Files.copy(screenshotFile.toPath(), goldenImage.toPath(), StandardCopyOption.REPLACE_EXISTING)
		}

		assertEquals(
			IDENTICAL_IMAGES,
			compareResult,
			"Fail: Screenshot are not identical.\n Current: ${screenshotFile.absolutePath}\n Golden: ${goldenImage.absolutePath}",
		)
	}

	private fun readGoldenImageFromSrcDir(screenshotFileName: String): File {
		val sourceDirectory =
			testClass.name
				.split(".") // split class name into parts
				.dropLast(1) // remove class name
				.joinToString("/") // convert to directory path

		val parentScreenshotDir = File(goldenImageDirectory.absolutePath.plus("/$sourceDirectory/screenshots/"))
		parentScreenshotDir.mkdir()

		return File(parentScreenshotDir, screenshotFileName)
	}

	companion object Companion {
		private const val IMAGE_QUALITY = 100
		private const val IDENTICAL_IMAGES = 0
		private const val IMAGES_DOES_NOT_EXIST = -1
	}
}
