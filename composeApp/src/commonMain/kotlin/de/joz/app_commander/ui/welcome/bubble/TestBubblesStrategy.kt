package de.joz.app_commander.ui.welcome.bubble

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

object TestBubblesStrategy : BubblesStrategy {
    override fun drawBubbles(
        drawScope: DrawScope,
        size: Size,
        step: Float,
    ) {
        drawScope.drawOval(
            color = Color.Green,
            topLeft =
                Offset(
                    x = 100f,
                    y = 100f,
                ),
            size = Size(200f, 200f),
        )
        drawScope.drawOval(
            color = Color.Yellow,
            topLeft =
                Offset(
                    x = 500f,
                    y = 800f,
                ),
            size = Size(150f, 150f),
        )
        drawScope.drawOval(
            color = Color.Blue,
            topLeft =
                Offset(
                    x = 300f,
                    y = 1500f,
                ),
            size = Size(300f, 300f),
        )
    }
}
