package de.joz.appcommander.helper.screenshot

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

class ImageConverter {
	fun convertToPng(bitmap: Bitmap): ByteArray? =
		Image.makeFromBitmap(bitmap).encodeToData(EncodedImageFormat.PNG, IMAGE_QUALITY)?.bytes

	private companion object {
		private const val IMAGE_QUALITY = 100
	}
}
