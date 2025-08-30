package de.joz.appcommander.ui.welcome.bubble

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class FadingInBubblesStrategy : BubblesStrategy {
    private val darkOrange = Color(red = 199, green = 110, blue = 0).copy(alpha = 0.5f)
    private val bubbles =
        BubblesStrategy.createRandomBubbles(
            color = darkOrange,
        )

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

    private fun createHexagon(
        bubble: Bubble,
        step: Float,
    ): Path {
        return Path().apply {
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
    }

    companion object {
        private const val RADIANT = 2 * PI / 6
    }
}
