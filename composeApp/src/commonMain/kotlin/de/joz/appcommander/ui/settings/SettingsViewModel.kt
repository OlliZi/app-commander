package de.joz.appcommander.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.joz.appcommander.domain.GetPreferenceUseCase
import de.joz.appcommander.domain.SavePreferenceUseCase
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.settings_preference_show_welcome_screen
import de.joz.appcommander.ui.misc.UnidirectionalDataFlowViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val savePreferenceUseCase: SavePreferenceUseCase,
    private val getPreferenceUseCase: GetPreferenceUseCase,
) : ViewModel(),
    UnidirectionalDataFlowViewModel<SettingsViewModel.UiState, SettingsViewModel.Event> {

    private val _uiState = MutableStateFlow(UiState())
    override val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(
                    togglePreferences = listOf(
                        ToggleItem(
                            label = Res.string.settings_preference_show_welcome_screen,
                            key = HIDE_WELCOME_SCREEN_PREF_KEY,
                            isChecked = getPreferenceUseCase.get(
                                key = HIDE_WELCOME_SCREEN_PREF_KEY,
                                defaultValue = false,
                            )
                        )
                    ),
                )
            }
        }
    }

    override fun onEvent(event: Event) {
        viewModelScope.launch {
            when (event) {
                is Event.OnToggleItem -> toggleItem(event)
            }
        }
    }

    private suspend fun toggleItem(event: Event.OnToggleItem) {
        savePreferenceUseCase(HIDE_WELCOME_SCREEN_PREF_KEY, event.isChecked)
        _uiState.update { oldState ->
            oldState.copy(
                togglePreferences = oldState.togglePreferences.map {
                    if (event.toggleItem == it) {
                        it.copy(isChecked = event.isChecked)
                    } else {
                        it
                    }
                }
            )

        }
    }

    sealed interface Event {
        data class OnToggleItem(
            val toggleItem: ToggleItem,
            val isChecked: Boolean,
        ) : Event
    }

    data class UiState(
        val togglePreferences: List<ToggleItem> = emptyList(),
    )

    data class ToggleItem(
        val isChecked: Boolean,
        val label: StringResource,
        val key: String,
    )

    companion object {
        const val HIDE_WELCOME_SCREEN_PREF_KEY = "HIDE_WELCOME_SCREEN_PREF_KEY"
    }
}