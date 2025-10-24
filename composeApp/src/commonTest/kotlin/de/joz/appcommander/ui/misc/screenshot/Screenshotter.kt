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

@OptIn(ExperimentalTestApi::class)
class Screenshotter(
	private val storeDirectory: File = File("./build/reports/tests/screenshots/"),
) {
	init {
		storeDirectory.mkdirs()
	}

	fun screenshot(
		source: ComposeUiTest,
		name: String,
		quality: Int = 100,
	): ScreenshotResult =
		screenshot(
			source = source.onNode(isRoot()),
			name = name,
			quality = quality,
		)

	fun screenshot(
		source: SemanticsNodeInteraction,
		name: String,
		quality: Int = 100,
	): ScreenshotResult =
		runCatching {
			val image = source.captureToImage()
			val bytearray = encodeToBytes(image = image, quality = quality)

			if (bytearray == null || bytearray.isEmpty()) {
				throw Exception("Screenshot is empty")
			}

			val file = File(storeDirectory, "$name.png")
			file.writeBytes(bytearray)
			println("Screenshot taken successfully: ${file.absolutePath}")
			ScreenshotResult.Success(screenshot = file)
		}.getOrElse { throwable ->
			println("Screenshot failed: ${throwable.message}")
			ScreenshotResult.Failure(error = throwable)
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
