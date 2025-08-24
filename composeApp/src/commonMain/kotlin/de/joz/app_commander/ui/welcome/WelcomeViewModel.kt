package de.joz.app_commander.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.joz.app_commander.domain.GetPreferenceUseCase
import de.joz.app_commander.domain.NavigationScreens
import de.joz.app_commander.domain.SavePreferenceUseCase
import de.joz.app_commander.ui.misc.UnidirectionalDataFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel(
    private val navController: NavController,
    private val savePreferenceUseCase: SavePreferenceUseCase,
    private val getPreferenceUseCase: GetPreferenceUseCase,
) : ViewModel(),
    UnidirectionalDataFlow<WelcomeViewModel.UiState, WelcomeViewModel.Event> {

    private val _viewState = MutableStateFlow(UiState())
    override val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            if (getPreferenceUseCase.get(HIDE_WELCOME_SCREEN_PREF_KEY, defaultValue = false)) {
                navController.navigate(NavigationScreens.ScriptsScreen)
            }
        }
    }

    override fun onEvent(event: Event) {
        viewModelScope.launch {
            when (event) {
                Event.OnNavigateToScripts -> {
                    navController.navigate(NavigationScreens.ScriptsScreen)
                }

                is Event.OnDoNotShowWelcomeAgain -> {
                    savePreferenceUseCase(HIDE_WELCOME_SCREEN_PREF_KEY, value = event.value)
                }
            }
        }
    }

    sealed interface Event {
        object OnNavigateToScripts : Event
        data class OnDoNotShowWelcomeAgain(val value: Boolean) : Event
    }

    data class UiState(
        val todo: Boolean = false,
    )

    companion object {
        private const val HIDE_WELCOME_SCREEN_PREF_KEY = "HIDE_WELCOME_SCREEN_PREF_KEY"
    }
}