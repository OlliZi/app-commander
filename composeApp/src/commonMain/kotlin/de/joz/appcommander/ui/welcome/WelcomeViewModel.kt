package de.joz.appcommander.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.joz.appcommander.domain.NavigationScreens
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import de.joz.appcommander.ui.misc.UnidirectionalDataFlowViewModel
import de.joz.appcommander.ui.settings.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class WelcomeViewModel(
	@InjectedParam private val navController: NavController,
	private val savePreferenceUseCase: SavePreferenceUseCase,
) : ViewModel(),
	UnidirectionalDataFlowViewModel<Unit, WelcomeViewModel.Event> {
	private val _uiState = MutableStateFlow(Unit)
	override val uiState = _uiState.asStateFlow()

	override fun onEvent(event: Event) {
		viewModelScope.launch {
			when (event) {
				Event.OnNavigateToScripts -> {
					navController.navigate(NavigationScreens.ScriptsScreen)
				}

				is Event.OnDoNotShowWelcomeAgain -> {
					savePreferenceUseCase(
						SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY,
						value = event.value,
					)
				}
			}
		}
	}

	sealed interface Event {
		object OnNavigateToScripts : Event

		data class OnDoNotShowWelcomeAgain(
			val value: Boolean,
		) : Event
	}
}
