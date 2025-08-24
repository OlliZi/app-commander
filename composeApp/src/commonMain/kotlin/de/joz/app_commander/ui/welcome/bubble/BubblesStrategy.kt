package de.joz.app_commander.ui.welcome.bubble

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.max
import kotlin.random.Random

interface BubblesStrategy {
    fun drawBubbles(
        drawScope: DrawScope,
        size: Size,
        step: Float,
    )

    companion object {
        private const val BUBBLE_COUNT = 50
        private const val MIN_BUBBLE_COLOR_ALPHA = 0.3f
        private const val MAX_SIZE = 200f
        private const val MIN_SIZE = 30f
        private val BUBBLE_COLOR = Color.Green.copy(green = 0.5f)
        private val RANDOM = Random(1)

        fun createRandomBubbles(color: Color = BUBBLE_COLOR): List<Bubble> {
            return List(BUBBLE_COUNT) {
                Bubble(
                    color =
                        color.copy(
                            alpha =
                                max(
                                    MIN_BUBBLE_COLOR_ALPHA,
                                    RANDOM.nextFloat(),
                                ),
                        ),
                    size = max(MIN_SIZE, RANDOM.nextFloat() * MAX_SIZE),
                    x = RANDOM.nextFloat(),
                    y = RANDOM.nextFloat(),
                )
            }
        }
    }
}

data class Bubble(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
)
