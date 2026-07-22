package de.joz.appcommander.helper.screenshot

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

class ImageConverter {
	fun convertToPng(bitmap: Bitmap): ByteArray? =
		Image.makeFromBitmap(bitmap).encodeToData(EncodedImageFormat.PNG, IMAGE_QUALITY)?.bytes

	fun trimHeight(bitmap: Bitmap): Bitmap {
		val backgroundColor = bitmap.getColor(bitmap.width - 1, bitmap.height - 1)
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

		if (lastContentRow == 0) return bitmap

		val trimmedHeight = lastContentRow.coerceAtMost(bitmap.height)
		val trimmedBitmap = Bitmap()
		trimmedBitmap.allocPixels(bitmap.imageInfo.withHeight(trimmedHeight))

		val canvas = Canvas(trimmedBitmap)
		canvas.drawImage(Image.makeFromBitmap(bitmap), 0f, 0f)

		return trimmedBitmap
	}

	private companion object {
		private const val IMAGE_QUALITY = 100
	}
}
