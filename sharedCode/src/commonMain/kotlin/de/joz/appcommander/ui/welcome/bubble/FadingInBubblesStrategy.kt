package de.joz.appcommander.ui.welcome.bubble

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import org.koin.core.annotation.Factory
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

@Factory
class FadingInBubblesStrategy : BubblesStrategy {
	private val bubbles = createRandomBubbles()

	override fun drawBubbles(
		drawScope: DrawScope,
		size: Size,
		step: Float,
	) {
		bubbles.forEach { bubble ->
			drawScope.translate(
				left = size.width * bubble.x,
				top = size.height * bubble.y,
			) {
				drawPath(
					path = createHexagon(bubble, step),
					color = bubble.color,
				)
			}
		}
	}

	private fun createRandomBubbles(color: Color = BUBBLE_COLOR): List<Bubble> =
		List(BUBBLE_COUNT) {
			Bubble(
				color = color.copy(
					alpha = min(
						MAX_BUBBLE_COLOR_ALPHA,
						max(
							MIN_BUBBLE_COLOR_ALPHA,
							RANDOM.nextFloat(),
						),
					),
				),
				size = max(MIN_SIZE, RANDOM.nextFloat() * MAX_SIZE),
				x = RANDOM.nextFloat(),
				y = RANDOM.nextFloat(),
			)
		}

	private fun createHexagon(
		bubble: Bubble,
		step: Float,
	): Path =
		Path().apply {
			(0..6).forEach {
				val r = bubble.size / 2 * max(0f, step)
				val x = bubble.x + r * cos(RADIANT * it).toFloat()
				val y = bubble.y + r * sin(RADIANT * it).toFloat()
				if (isEmpty) {
					moveTo(x, y)
				} else {
					lineTo(x, y)
				}
			}
			close()
		}

	companion object {
		private const val RADIANT = 2 * PI / 6
		private const val BUBBLE_COUNT = 50
		private const val MIN_BUBBLE_COLOR_ALPHA = 0.3f
		private const val MAX_BUBBLE_COLOR_ALPHA = 0.6f
		private const val MAX_SIZE = 200f
		private const val MIN_SIZE = 50f
		private val BUBBLE_COLOR = Color.Green.copy(green = 0.5f)
		private val RANDOM = Random(1)
	}
}
