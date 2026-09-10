package de.joz.appcommander.ui.welcome.animation

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
class FadingInAnimationStrategy : AnimationStrategy {
	private val tokens = createRandomTokens()

	override fun render(
		drawScope: DrawScope,
		size: Size,
		step: Float,
	) {
		tokens.forEach { token ->
			drawScope.translate(
				left = size.width * token.x,
				top = size.height * token.y,
			) {
				drawPath(
					path = createHexagon(token, step),
					color = token.color,
				)
			}
		}
	}

	private fun createRandomTokens(): List<Token> =
		List(TOKEN_COUNT) {
			Token(
				color = TOKEN_COLOR.copy(
					alpha = min(
						MAX_TOKEN_COLOR_ALPHA,
						max(
							MIN_TOKEN_COLOR_ALPHA,
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
		token: Token,
		step: Float,
	): Path =
		Path().apply {
			(0..6).forEach {
				val r = token.size / 2 * max(0f, step)
				val x = token.x + r * cos(RADIANT * it).toFloat()
				val y = token.y + r * sin(RADIANT * it).toFloat()
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
		private const val TOKEN_COUNT = 50
		private const val MIN_TOKEN_COLOR_ALPHA = 0.3f
		private const val MAX_TOKEN_COLOR_ALPHA = 0.6f
		private const val MAX_SIZE = 200f
		private const val MIN_SIZE = 50f
		private val TOKEN_COLOR = Color.Green.copy(green = 0.5f)
		private val RANDOM = Random(1)
	}
}
