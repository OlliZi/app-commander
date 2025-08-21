package de.joz.app_commander

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.joz.app_commander.domain.ExecuteScriptUseCase

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "App-Commander",
    ) {
        val executeScriptUseCase = ExecuteScriptUseCase()

        App(
            executeScriptUseCase = executeScriptUseCase
        )
    }
}