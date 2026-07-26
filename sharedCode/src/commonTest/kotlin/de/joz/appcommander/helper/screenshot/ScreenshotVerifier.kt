package de.joz.appcommander.helper.screenshot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import de.joz.appcommander.helper.IsJenkinsTestRunUseCase
import de.joz.appcommander.helper.IsLocalTestRunUseCase
import org.jetbrains.skia.Bitmap
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
	private val imageConverter: ImageConverter = ImageConverter(),
	private val screenshotter: Screenshotter = Screenshotter(imageConverter),
) {
	init {
		storeDirectory.mkdirs()
	}

	fun verifyScreenshot(
		source: ComposeUiTest,
		screenshotName: String,
		errorCollector: ((String) -> Unit)? = null,
	) {
		val screenshotResult = screenshotter.takeScreenshot(
			source = source,
			screenshotName = screenshotName,
			storeDirectory = storeDirectory,
		)

		when (screenshotResult) {
			is ScreenshotResult.Success -> {
				verifyAgainstGoldenImage(
					screenshotFile = screenshotResult.screenshot,
					errorCollector = { error ->
						errorCollector?.let { errorCollector(error) } ?: fail(error)
					},
				)
			}

			is ScreenshotResult.Failure -> {
				throw screenshotResult.error
			}
		}
	}

	private fun verifyAgainstGoldenImage(
		screenshotFile: File,
		errorCollector: ((String) -> Unit),
	) {
		val goldenImage = readGoldenImageFromSrcDir(
			screenshotFileName = screenshotFile.name,
		)

		if (!goldenImage.exists()) {
			Files.copy(screenshotFile.toPath(), goldenImage.toPath())
			errorCollector(
				"Golden image does not exist. Copied for your. Check your VCS.\n" + "Current: ${screenshotFile.absolutePath}\n" +
					"Golden: ${goldenImage.absolutePath}",
			)
		}

		val result = createScreenshotDifferenceUseCase(
			currentScreenshot = createBitmapFromScreenshot(screenshotFile = screenshotFile),
			goldenScreenshot = createBitmapFromScreenshot(screenshotFile = goldenImage),
		)
		when (result) {
			is CreateScreenshotDifferenceUseCase.Result.IdenticalScreenshots -> {
				// success case
			}

			is CreateScreenshotDifferenceUseCase.Result.SizeDoesNotMatch -> {
				errorCollector(
					"Screenshot size does not match golden image size. " +
						"Fix test or replace golden image with current screenshot.\n" +
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
					diffFile.writeBytes(imageConverter.convertToPng(result.diffBitmap)!!)
					val currentScreenshot = File(goldenImage.parentFile, goldenImage.name)
					Files.copy(screenshotFile.toPath(), currentScreenshot.toPath(), StandardCopyOption.REPLACE_EXISTING)

					errorCollector(
						"Screenshots differs. Take a look at the diff image.\n" + "Fraction: ${result.fraction}\n" +
							"Diff: ${diffFile.absolutePath}\n" +
							"Current: ${screenshotFile.absolutePath}\n" +
							"Golden: ${goldenImage.absolutePath}",
					)
				}
			}
		}
	}

	private fun readGoldenImageFromSrcDir(screenshotFileName: String): File {
		val sourceDirectory = testClass.name
			.split(".") // split class name into parts
			.dropLast(1) // remove class name
			.joinToString("/") // convert to directory path

		val parentScreenshotDir = File(goldenImageDirectory.absolutePath.plus("/$sourceDirectory/screenshots/"))
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

	companion object Companion {
		private const val IMAGE_DIFF_THRESHOLD = 0.01f // 1 %
	}
}
