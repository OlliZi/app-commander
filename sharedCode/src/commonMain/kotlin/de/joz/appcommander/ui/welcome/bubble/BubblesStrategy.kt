package de.joz.appcommander.ui.welcome.bubble

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

interface BubblesStrategy {
	fun drawBubbles(
		drawScope: DrawScope,
		size: Size,
		step: Float,
	)
}

data class Bubble(
	val x: Float,
	val y: Float,
	val size: Float,
	val color: Color,
)
