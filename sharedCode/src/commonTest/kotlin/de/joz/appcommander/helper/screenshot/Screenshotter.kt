package de.joz.appcommander.helper.screenshot

import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isRoot
import java.io.File

@OptIn(ExperimentalTestApi::class)
class Screenshotter(
	private val imageConverter: ImageConverter,
) {
	fun takeScreenshot(
		source: ComposeUiTest,
		screenshotName: String,
		storeDirectory: File,
	): ScreenshotResult =
		runCatching {
			val dialog = source.onNode(isDialog())

			val node = try {
				if (dialog.isDisplayed()) {
					dialog
				} else {
					source.onNode(isRoot())
				}
			} catch (_: Throwable) {
				source.onNode(isRoot())
			}

			val pngByteArray = imageConverter.convertToPng(node.captureToImage().asSkiaBitmap())

			if (pngByteArray == null || pngByteArray.isEmpty()) {
				throw Exception("Screenshot is empty.")
			}

			val file = File(storeDirectory, "$screenshotName.png")
			file.writeBytes(pngByteArray)
			ScreenshotResult.Success(screenshot = file)
		}.getOrElse { throwable ->
			ScreenshotResult.Failure(error = throwable)
		}
}
