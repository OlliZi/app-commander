package de.joz.appcommander.helper.screenshot

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

class ImageConverter {
	fun convertToPng(bitmap: Bitmap): ByteArray? =
		Image.makeFromBitmap(bitmap).encodeToData(EncodedImageFormat.PNG, IMAGE_QUALITY)?.bytes

	fun trim(bitmap: Bitmap): Bitmap {
		val backgroundColor = bitmap.getColor(0, 0)
		val trimmedHeight = trimHeight(bitmap, backgroundColor)
		return trimmedWidth(trimmedHeight, backgroundColor)
	}

	private fun trimHeight(
		bitmap: Bitmap,
		backgroundColor: Int,
	): Bitmap {
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
			val trimmedBitmap = Bitmap()
			val trimmedHeight = (lastContentRow + PADDING).coerceAtMost(bitmap.height)
			trimmedBitmap.allocPixels(bitmap.imageInfo.withHeight(trimmedHeight))
			renderBitmap(bitmap, trimmedBitmap)
			trimmedBitmap
		}
	}

	private fun trimmedWidth(
		bitmap: Bitmap,
		backgroundColor: Int,
	): Bitmap {
		var lastContentColumn = 0

		for (x in bitmap.width - 1 downTo 0) {
			var isColumnEmpty = true
			for (y in 0 until bitmap.height - 1) {
				if (bitmap.getColor(x, y) != backgroundColor) {
					isColumnEmpty = false
					break
				}
			}
			if (!isColumnEmpty) {
				lastContentColumn = x
				break
			}
		}

		return if (lastContentColumn == 0) {
			bitmap
		} else {
			val trimmedBitmap = Bitmap()
			val trimmedWidth = (lastContentColumn + PADDING).coerceAtMost(bitmap.width)
			trimmedBitmap.allocPixels(bitmap.imageInfo.withWidth(trimmedWidth))
			renderBitmap(bitmap, trimmedBitmap)
			trimmedBitmap
		}
	}

	private fun renderBitmap(
		originalBitmap: Bitmap,
		trimmedBitmap: Bitmap,
	) {
		val canvas = Canvas(trimmedBitmap)
		canvas.drawImage(Image.makeFromBitmap(originalBitmap), 0f, 0f)
	}

	private companion object {
		private const val IMAGE_QUALITY = 100
		private const val PADDING = 16
	}
}
