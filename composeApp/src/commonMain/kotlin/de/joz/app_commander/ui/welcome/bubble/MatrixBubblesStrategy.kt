package de.joz.app_commander.ui.welcome.bubble

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.drawText

class MatrixBubblesStrategy : BubblesStrategy {
    private val bubbles = createMatrixBubbles()

    override fun drawBubbles(
        drawScope: DrawScope,
        size: Size,
        step: Float,
    ) {
        bubbles.forEach { bubble ->

            drawScope.drawOval(
                color = bubble.color,
                topLeft = Offset(
                    x = bubble.x,// * size.width - bubble.size / 4,
                    y = bubble.y * step,// * size.height * step,
                ),
                size = Size(bubble.size, bubble.size),
            )
        }
    }

    private fun createMatrixBubbles(): List<Bubble> {
        val size = 30f
        val padding = 10
        val color = Color.Green.copy(
            red = Color.Green.red * 0.5f,
            green = Color.Green.green * 0.5f,
            blue = Color.Green.blue * 0.5f
        )
        return buildList {
            (1..100).forEach { x ->
                (1..20).forEach { y ->
                    add(
                        Bubble(
                            x = x.toFloat() * (padding * 2 + size),
                            y = y.toFloat() * (padding * 2 + size),
                            size = size,
                            color = color,
                        )
                    )
                }
            }
        }
    }
}
