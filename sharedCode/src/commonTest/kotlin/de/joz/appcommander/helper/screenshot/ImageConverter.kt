package de.joz.appcommander.helper.screenshot

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import kotlin.math.abs

class ImageConverter {
	fun convertToPng(bitmap: Bitmap): ByteArray? =
		Image.makeFromBitmap(bitmap).encodeToData(EncodedImageFormat.PNG, IMAGE_QUALITY)?.bytes

	fun trim(bitmap: Bitmap): Bitmap {
		val backgroundColor = bitmap.getColor(0, 0)
		// val trimmedHeight = trimHeight(bitmap, backgroundColor)

		return trimmedWidth(bitmap, backgroundColor)
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
		val bounds = computeTrimBounds(bitmap, backgroundColor)
		val trimmedBitmap = Bitmap()
		val w = abs(bounds.right - bounds.left) + 2 * PADDING
		val h = abs(bounds.bottom - bounds.top) + 2 * PADDING
		trimmedBitmap.allocPixels(bitmap.imageInfo.withWidthHeight(w, h))

		val canvas = Canvas(trimmedBitmap)
		canvas.drawImage(Image.makeFromBitmap(bitmap), -bounds.left.toFloat() + PADDING, -bounds.top.toFloat() + PADDING)

		return trimmedBitmap
	}

	private fun computeTrimBounds(
		bitmap: Bitmap,
		backgroundColor: Int,
	): TrimBounds {
		var trimBounds = TrimBounds()

		for (x in 0 until bitmap.width) {
			var isLeftColumEmpty = true
			var isRightColumEmpty = true

			for (y in 0 until bitmap.height) {
				if (bitmap.getColor(x, y) != backgroundColor) {
					isLeftColumEmpty = false
					if (trimBounds.left == -1) {
						trimBounds = trimBounds.copy(left = x)
					}
				}
				if (bitmap.getColor(bitmap.width - 1 - x, y) != backgroundColor) {
					isRightColumEmpty = false
				}
			}

			if (!isLeftColumEmpty && trimBounds.left == -1) {
				// 	trimBounds = trimBounds.copy(left = x)
			}
			if (!isRightColumEmpty && trimBounds.right == -1) {
				trimBounds = trimBounds.copy(right = bitmap.width - 1 - x)
			}
		}

		for (y in 0 until bitmap.height) {
			var isTopRorEmpty = true
			var isBottomRowEmpty = true

			for (x in 0 until bitmap.width) {
				if (bitmap.getColor(x, y) != backgroundColor) {
					isTopRorEmpty = false
				}
				if (bitmap.getColor(x, bitmap.height - 1 - y) != backgroundColor) {
					isBottomRowEmpty = false
				}
			}

			if (!isTopRorEmpty && trimBounds.top == -1) {
				trimBounds = trimBounds.copy(top = y)
			}
			if (!isBottomRowEmpty && trimBounds.bottom == -1) {
				trimBounds = trimBounds.copy(bottom = bitmap.height - 1 - y)
			}
		}

		return trimBounds
	}

	private fun renderBitmap(
		originalBitmap: Bitmap,
		trimmedBitmap: Bitmap,
	) {
		val canvas = Canvas(trimmedBitmap)
		canvas.drawImage(Image.makeFromBitmap(originalBitmap), 0f, 0f)
	}

	data class TrimBounds(
		val left: Int = -1,
		val right: Int = -1,
		val top: Int = -1,
		val bottom: Int = -1,
	)

	private companion object {
		private const val IMAGE_QUALITY = 100
		private const val PADDING = 16
	}
}
