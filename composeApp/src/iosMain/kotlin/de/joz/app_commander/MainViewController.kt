package de.joz.app_commander

import androidx.compose.ui.window.ComposeUIViewController
import de.joz.app_commander.domain.ExecuteScriptUseCase

fun MainViewController() = ComposeUIViewController {
    val executeScriptUseCase = ExecuteScriptUseCase()

    App(
        executeScriptUseCase = executeScriptUseCase,
    )
}