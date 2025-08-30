package de.joz.appcommander.ui.welcome.bubble

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope

class FallingBubblesStrategy : BubblesStrategy {
    private val bubbles = BubblesStrategy.createRandomBubbles()

    override fun drawBubbles(
        drawScope: DrawScope,
        size: Size,
        step: Float,
    ) {
        bubbles.forEach { bubble ->
            drawScope.drawOval(
                color = bubble.color,
                topLeft =
                    Offset(
                        x = bubble.x * size.width - bubble.size / 4,
                        y = bubble.y * size.height * step,
                    ),
                size = Size(bubble.size, bubble.size),
            )
        }
    }
}
