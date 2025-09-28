package de.joz.appcommander.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColors {
    val GREEN_MAIN = Color(0xFF6ab090)
}

val darkColorScheme =
    darkColorScheme(
        onBackground = Color.White,
        onPrimary = Color.White,
        surface = Color.Black,
        onSurface = Color.White,
        primary = AppColors.GREEN_MAIN,
    )

val lightColorScheme =
    lightColorScheme(
        onBackground = Color.Black,
        onPrimary = Color.Black,
        surface = Color.White,
        onSurface = Color.Black,
        primary = AppColors.GREEN_MAIN,
    )

@Composable
internal fun AppCommanderTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme else lightColorScheme,
        content = content,
    )
}
