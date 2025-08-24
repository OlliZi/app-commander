package de.joz.app_commander

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import de.joz.app_commander.data.PreferencesRepositoryImpl
import de.joz.app_commander.data.getDataStore
import de.joz.app_commander.domain.ExecuteScriptUseCase
import de.joz.app_commander.domain.GetPreferenceUseCase
import de.joz.app_commander.domain.SavePreferenceUseCase
import de.joz.app_commander.resources.Res
import de.joz.app_commander.resources.app_name
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(500.dp, 800.dp)
    )

    Window(
        state = windowState,
        title = stringResource(Res.string.app_name),
        onCloseRequest = ::exitApplication,
    ) {
        val preferencesRepository = PreferencesRepositoryImpl(
            dataStore = getDataStore(),
        )
        val executeScriptUseCase = ExecuteScriptUseCase()
        val savePreferenceUseCase = SavePreferenceUseCase(
            preferencesRepository = preferencesRepository,
        )
        val getPreferenceUseCase = GetPreferenceUseCase(
            preferencesRepository = preferencesRepository,
        )

        App(
            executeScriptUseCase = executeScriptUseCase,
            savePreferenceUseCase = savePreferenceUseCase,
            getPreferenceUseCase = getPreferenceUseCase,
        )
    }
}