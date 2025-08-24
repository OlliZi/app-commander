package de.joz.app_commander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.joz.app_commander.data.DataStoreAndroidImpl
import de.joz.app_commander.data.PreferencesRepositoryImpl
import de.joz.app_commander.data.getDataStore
import de.joz.app_commander.domain.ExecuteScriptUseCase
import de.joz.app_commander.domain.GetPreferenceUseCase
import de.joz.app_commander.domain.SavePreferenceUseCase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val executeScriptUseCase = ExecuteScriptUseCase()

        setContent {
            DataStoreAndroidImpl.dummycontext = this
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
}
