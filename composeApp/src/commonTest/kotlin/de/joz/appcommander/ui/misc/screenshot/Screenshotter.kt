package de.joz.appcommander.ui.misc.screenshot

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
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

	fun screenshot(
		source: ComposeUiTest,
		screenshotName: String,
		quality: Int = 100,
	): ScreenshotResult =
		screenshot(
			source = source.onNode(isRoot()),
			screenshotName = screenshotName,
			quality = quality,
		)

	fun screenshot(
		source: SemanticsNodeInteraction,
		screenshotName: String,
		quality: Int = 100,
	): ScreenshotResult =
		runCatching {
			val image = source.captureToImage()
			val bytearray = encodeToBytes(image = image, quality = quality)

			if (bytearray == null || bytearray.isEmpty()) {
				throw Exception("Screenshot is empty")
			}

			val file = File(storeDirectory, "$screenshotName.png")
			file.writeBytes(bytearray)
			println("Screenshot taken successfully: ${file.absolutePath}")
			ScreenshotResult.Success(screenshot = file)
		}.getOrElse { throwable ->
			println("Screenshot failed: ${throwable.message}")
			ScreenshotResult.Failure(error = throwable)
		}

	fun verify(
		test: Any,
		screenshotResult: ScreenshotResult,
	) {
		when (screenshotResult) {
			is ScreenshotResult.Success ->
				innerVerify(
					test = test, // TODO besser machen
					screenshotFile = screenshotResult.screenshot,
				)

			is ScreenshotResult.Failure -> throw screenshotResult.error
		}
	}

	private fun innerVerify(
		test: Any,
		screenshotFile: File,
	) {
		val goldenImage =
			readFromTestDir(
				test = test,
				screenshotFile.name,
			)

		assertEquals(
			0,
			Arrays.compare(screenshotFile.readBytes(), goldenImage.readBytes()),
			"Fail: Screenshot are not identical.\n Current: ${screenshotFile.absolutePath}\n Golden: ${goldenImage.absolutePath}",
		)
	}

	private fun readFromTestDir(
		test: Any,
		name: String,
	): File {
		val dir =
			test.javaClass.name
				.split(".")
				.dropLast(1)
				.joinToString("/")
				.replace(".", "/")
		val goldenIMage = File(goldenImageDirectory.absolutePath + "/" + dir + "/", name)
		return goldenIMage
	}

	private fun encodeToBytes(
		image: ImageBitmap,
		quality: Int,
	): ByteArray? =
		Image
			.makeFromBitmap(image.asSkiaBitmap())
			.encodeToData(EncodedImageFormat.PNG, quality)
			?.bytes
}
