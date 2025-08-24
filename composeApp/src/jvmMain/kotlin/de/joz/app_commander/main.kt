package de.joz.app_commander

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import de.joz.app_commander.resources.Res
import de.joz.app_commander.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.ksp.generated.*

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(500.dp, 800.dp)
    )

    Window(
        state = windowState,
        title = stringResource(Res.string.app_name),
        onCloseRequest = ::exitApplication,
    ) {
        KoinApplication(
            application = {
                modules(DependencyInjection().module)
            }
        ) {
            App()
        }
    }
}