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
		val backgroundColorLeft = bitmap.getColor(0, 0)
		val backgroundColorRight = bitmap.getColor(bitmap.width - 1, 0)
		val backgroundColorTop = bitmap.getColor(0, 0)
		val backgroundColorBottom = bitmap.getColor(0, bitmap.height - 1)

		val bounds = computeTrimBounds(
			bitmap = bitmap,
			backgroundColorLeft = backgroundColorLeft,
			backgroundColorRight = backgroundColorRight,
			backgroundColorTop = backgroundColorTop,
			backgroundColorBottom = backgroundColorBottom,
		)

		val w = abs(bounds.right - bounds.left) + 2 * PADDING
		val h = abs(bounds.bottom - bounds.top) + 2 * PADDING

		val trimmedBitmap = Bitmap()
		trimmedBitmap.allocPixels(bitmap.imageInfo.withWidthHeight(w, h))

		val canvas = Canvas(trimmedBitmap)
		canvas.drawImage(
			Image.makeFromBitmap(bitmap),
			-bounds.left.toFloat() + PADDING,
			-bounds.top.toFloat() + PADDING,
		)
		return trimmedBitmap
	}

	private fun computeTrimBounds(
		bitmap: Bitmap,
		backgroundColorLeft: Int,
		backgroundColorRight: Int,
		backgroundColorTop: Int,
		backgroundColorBottom: Int,
	): TrimBounds {
		var trimBounds = TrimBounds()

		for (x in 0 until bitmap.width) {
			for (y in 0 until bitmap.height) {
				if (bitmap.getColor(x, y) != backgroundColorLeft && trimBounds.left == -1) {
					trimBounds = trimBounds.copy(left = x)
				}
				if (bitmap.getColor(bitmap.width - 1 - x, y) != backgroundColorRight && trimBounds.right == -1) {
					trimBounds = trimBounds.copy(right = bitmap.width - 1 - x)
				}
			}
		}

		for (y in 0 until bitmap.height) {
			for (x in 0 until bitmap.width) {
				if (bitmap.getColor(x, y) != backgroundColorTop && trimBounds.top == -1) {
					trimBounds = trimBounds.copy(top = y)
				}
				if (bitmap.getColor(x, bitmap.height - 1 - y) != backgroundColorBottom && trimBounds.bottom == -1) {
					trimBounds = trimBounds.copy(bottom = bitmap.height - 1 - y)
				}
			}
		}

		return trimBounds
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
