package de.joz.appcommander.ui.misc.screenshot

import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.io.File
import java.util.Arrays
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class Screenshotter(
	private val storeDirectory: File = File("./build/reports/tests/screenshots/"),
	private val goldenImageDirectory: File = File("./src/commonTest/kotlin/"),
) {
	init {
		storeDirectory.mkdirs()
	}

	fun <T> verifyScreenshot(
		testClass: Class<T>,
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
					testClass = testClass, // TODO besser machen
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

	private fun <T> innerVerify(
		testClass: Class<T>,
		screenshotFile: File,
	) {
		val goldenImage =
			readGoldenImageFromSrcDir(
				testClass = testClass,
				screenshotFileName = screenshotFile.name,
			)

		assertEquals(
			0,
			Arrays.compare(screenshotFile.readBytes(), goldenImage.readBytes()),
			"Fail: Screenshot are not identical.\n Current: ${screenshotFile.absolutePath}\n Golden: ${goldenImage.absolutePath}",
		)
	}

	private fun <T> readGoldenImageFromSrcDir(
		testClass: Class<T>,
		screenshotFileName: String,
	): File {
		val sourceDirectory =
			testClass.name
				.split(".") // split class name into parts
				.dropLast(1) // remove class name
				.joinToString("/") // convert to directory path

		return File(goldenImageDirectory.absolutePath.plus("/$sourceDirectory/"), screenshotFileName)
	}

	companion object {
		private const val IMAGE_QUALITY = 100
	}
}
