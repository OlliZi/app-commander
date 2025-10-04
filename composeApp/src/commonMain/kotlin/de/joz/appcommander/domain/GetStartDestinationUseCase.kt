package de.joz.appcommander.domain

import de.joz.appcommander.ui.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Factory

@Factory
class GetStartDestinationUseCase(
    private val getPreferenceUseCase: GetPreferenceUseCase,
) {
    operator fun invoke(scope: CoroutineScope): NavigationScreens =
        runBlocking {
            getStartDestination()
        }

    private suspend fun getStartDestination(): NavigationScreens =
        if (getPreferenceUseCase.get(
                key = SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY,
                defaultValue = false,
            )
        ) {
            NavigationScreens.ScriptsScreen
        } else {
            NavigationScreens.WelcomeScreen
        }
}
