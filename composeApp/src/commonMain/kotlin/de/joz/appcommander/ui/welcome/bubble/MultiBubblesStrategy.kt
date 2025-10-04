package de.joz.appcommander.ui.welcome.bubble

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.koin.core.annotation.Factory

@Factory
class MultiBubblesStrategy : BubblesStrategy {
    private val clients =
        listOf(
            FallingBubblesStrategy(),
            FadingInBubblesStrategy(),
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
