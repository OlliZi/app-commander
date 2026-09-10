package de.joz.appcommander.ui.welcome.animation

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

interface AnimationStrategy {
	fun render(
		drawScope: DrawScope,
		size: Size,
		step: Float,
	)
}

data class Token(
	val x: Float,
	val y: Float,
	val size: Float,
	val color: Color,
)
