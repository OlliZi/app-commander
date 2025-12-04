package de.joz.appcommander.helper

import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.test.fail

@OptIn(ExperimentalTestApi::class)
class ScreenshotVerifier<T>(
	private val testClass: Class<T>,
	private val storeDirectory: File = File("./build/reports/tests/screenshots/"),
	private val goldenImageDirectory: File = File("./src/commonTest/kotlin/"),
	private val isLocalTestRunUseCase: IsLocalTestRunUseCase = IsLocalTestRunUseCase(),
	private val isJenkinsTestRunUseCase: IsJenkinsTestRunUseCase = IsJenkinsTestRunUseCase(),
	private val createScreenshotDifferenceUseCase: CreateScreenshotDifferenceUseCase = CreateScreenshotDifferenceUseCase(),
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
			is ScreenshotResult.Success -> {
				verifyAgainstGoldenImage(
					screenshotFile = screenshotResult.screenshot,
				)
			}

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

		if (!goldenImage.exists()) {
			Files.copy(screenshotFile.toPath(), goldenImage.toPath())
			fail(
				"Golden image does not exist. Copied for your. Check your VCS.\n" +
					"Current: ${screenshotFile.absolutePath}\n" +
					"Golden: ${goldenImage.absolutePath}",
			)
		}

		val result =
			createScreenshotDifferenceUseCase(
				currentScreenshot = createBitmapFromScreenshot(screenshotFile = screenshotFile),
				goldenScreenshot = createBitmapFromScreenshot(screenshotFile = goldenImage),
			)
		when (result) {
			is CreateScreenshotDifferenceUseCase.Result.IdenticalScreenshots -> {
				// success case
			}

			is CreateScreenshotDifferenceUseCase.Result.SizeDoesNotMatch -> {
				fail(
					"Screenshot size does not match golden image size. Fix test or replace golden image with current screenshot.\n" +
						"Current: ${screenshotFile.absolutePath}\n" +
						"Golden: ${goldenImage.absolutePath}",
				)
			}

			is CreateScreenshotDifferenceUseCase.Result.ThresholdMatch -> {
				if (isLocalTestRunUseCase() || isJenkinsTestRunUseCase()) {
					println("Can run screenshot-tests only on github.")
					return
				}

				if (result.fraction > IMAGE_DIFF_THRESHOLD) {
					val diffFile = File(goldenImage.parentFile, "${goldenImage.nameWithoutExtension}_diff.png")
					diffFile.writeBytes(convertToPng(result.diffBitmap)!!)
					val currentScreenshot =
						File(goldenImage.parentFile, "${goldenImage.nameWithoutExtension}_current.png")
					Files.copy(screenshotFile.toPath(), currentScreenshot.toPath(), StandardCopyOption.REPLACE_EXISTING)
					fail(
						"Screenshots differs. Take a look at the diff image.\n" +
							"Fraction: ${result.fraction}\n" +
							"Diff: ${diffFile.absolutePath}\n" +
							"Current: ${screenshotFile.absolutePath}\n" +
							"Golden: ${goldenImage.absolutePath}",
					)
				}
			}
		}
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
		val image = Image.makeFromEncoded(screenshotFile.readBytes())

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
		private const val IMAGE_DIFF_THRESHOLD = 0.01f // 1 %
		private const val IMAGE_QUALITY = 100
	}
}
