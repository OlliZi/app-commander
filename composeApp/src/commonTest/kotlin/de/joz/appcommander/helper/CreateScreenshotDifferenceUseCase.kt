package de.joz.appcommander.helper

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Paint

class CreateScreenshotDifferenceUseCase {
	operator fun invoke(
		currentScreenshot: Bitmap,
		goldenScreenshot: Bitmap,
	): Result {
		if (currentScreenshot.width != goldenScreenshot.width || currentScreenshot.height != goldenScreenshot.height) {
			return Result.SizeDoesNotMatch
		}

		val diffBitmap = Bitmap()
		diffBitmap.allocPixels(currentScreenshot.imageInfo)
		val canvas = Canvas(diffBitmap)
		val highlightPaint =
			Paint().apply {
				color = 0xFFFF0000.toInt()
			}
		val normalPaint = Paint()
		var diffCount = 0

		for (x in 0 until currentScreenshot.width) {
			for (y in 0 until currentScreenshot.height) {
				val pixelColor1 = currentScreenshot.getColor(x = x, y = y)
				val pixelColor2 = goldenScreenshot.getColor(x = x, y = y)
				val paint =
					if (pixelColor1 == pixelColor2) {
						normalPaint
					} else {
						diffCount++
						highlightPaint
					}

				canvas.drawPoint(
					x.toFloat(),
					y.toFloat(),
					paint,
				)
			}
		}

		return if (diffCount == 0) {
			Result.IdenticalScreenshots
		} else {
			Result.ThresholdMatch(
				fraction = diffCount.toFloat() / (currentScreenshot.width * currentScreenshot.height),
				diffBitmap = diffBitmap,
			)
		}
	}

	sealed interface Result {
		object IdenticalScreenshots : Result

		object SizeDoesNotMatch : Result

		data class ThresholdMatch(
			val fraction: Float,
			val diffBitmap: Bitmap,
		) : Result
	}
}
