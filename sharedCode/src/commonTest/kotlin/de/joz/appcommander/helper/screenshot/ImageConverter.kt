package de.joz.appcommander.helper.screenshot

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

class ImageConverter {
	fun convertToPng(bitmap: Bitmap): ByteArray? =
		Image.makeFromBitmap(bitmap).encodeToData(EncodedImageFormat.PNG, IMAGE_QUALITY)?.bytes

	fun trimHeight(bitmap: Bitmap): Bitmap {
		val backgroundColor = bitmap.getColor(0, bitmap.height - 1)

		if (backgroundColor != bitmap.getColor(0, 0)) {
			// assume we have a full rendered screen
			return bitmap
		}

		var lastContentRow = 0

		for (y in bitmap.height - 1 downTo 0) {
			var isRowEmpty = true
			for (x in 0 until bitmap.width) {
				if (bitmap.getColor(x, y) != backgroundColor) {
					isRowEmpty = false
					break
				}
			}
			if (!isRowEmpty) {
				lastContentRow = y
				break
			}
		}

		return if (lastContentRow == 0) {
			bitmap
		} else {
			renderTrimmedImage(
				originalBitmap = bitmap,
				lastContentRow = lastContentRow,
			)
		}
	}

	private fun renderTrimmedImage(
		originalBitmap: Bitmap,
		lastContentRow: Int,
	): Bitmap {
		val trimmedBitmap = Bitmap()
		val trimmedHeight = (lastContentRow + BOTTOM_PADDING).coerceAtMost(originalBitmap.height)
		trimmedBitmap.allocPixels(originalBitmap.imageInfo.withHeight(trimmedHeight))

		val canvas = Canvas(trimmedBitmap)
		canvas.drawImage(Image.makeFromBitmap(originalBitmap), 0f, 0f)

		return trimmedBitmap
	}

	private companion object {
		private const val IMAGE_QUALITY = 100
		private const val BOTTOM_PADDING = 16
	}
}
