package de.joz.app_commander.ui.welcome.bubble

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope

class MultiBubblesStrategy : BubblesStrategy {
    private val clients = listOf(
        FallingBubblesStrategy(),
        BlinkingBubblesStrategy(),
    )

    override fun drawBubbles(
        drawScope: DrawScope,
        size: Size,
        step: Float,
    ) {
        clients.forEach {
            it.drawBubbles(drawScope, size, step)
        }
    }
}
