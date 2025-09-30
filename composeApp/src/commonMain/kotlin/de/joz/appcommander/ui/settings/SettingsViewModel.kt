package de.joz.appcommander.ui.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.joz.appcommander.domain.GetPreferenceUseCase
import de.joz.appcommander.domain.ManageUiSAppearanceUseCase
import de.joz.appcommander.domain.SavePreferenceUseCase
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.settings_preference_show_welcome_screen
import de.joz.appcommander.resources.settings_preference_track_scripts_file_delay_slider_label
import de.joz.appcommander.resources.settings_preference_ui_appearance_dark
import de.joz.appcommander.resources.settings_preference_ui_appearance_label
import de.joz.appcommander.resources.settings_preference_ui_appearance_light
import de.joz.appcommander.resources.settings_preference_ui_appearance_system
import de.joz.appcommander.ui.misc.UnidirectionalDataFlowViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    getPreferenceUseCase: GetPreferenceUseCase,
    private val savePreferenceUseCase: SavePreferenceUseCase,
    private val manageUiSAppearanceUseCase: ManageUiSAppearanceUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel(),
    UnidirectionalDataFlowViewModel<SettingsViewModel.UiState, SettingsViewModel.Event> {

    private val _uiState = MutableStateFlow(UiState())
    override val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
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
                    sliderPreferences = listOf(
                        SliderItem(
                            maximum = 10f,
                            minimum = 1f,
                            steps = 8,
                            sliderValue = getPreferenceUseCase.get(
                                key = TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY,
                                defaultValue = 5,
                            ).toFloat(),
                            label = Res.string.settings_preference_track_scripts_file_delay_slider_label,
                            key = TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY,
                        ),
                        SliderItem(
                            maximum = ManageUiSAppearanceUseCase.UiAppearance.entries.maxOf { it.optionIndex }
                                .toFloat(),
                            minimum = ManageUiSAppearanceUseCase.UiAppearance.entries.minOf { it.optionIndex }
                                .toFloat(),
                            steps = 1,
                            sliderValue = getPreferenceUseCase.get(
                                key = ManageUiSAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
                                defaultValue = ManageUiSAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex,
                            ).toFloat(),
                            label = Res.string.settings_preference_ui_appearance_label,
                            key = ManageUiSAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
                            labelValue = { sliderValue ->
                                mapUiAppearance(sliderValue)
                            }
                        ),
                    ),
                )
            }
        }
    }

    override fun onEvent(event: Event) {
        viewModelScope.launch {
            when (event) {
                is Event.OnToggleItem -> toggleItem(event)
                is Event.OnSliderItem -> sliderItem(event)
            }
        }
    }

    private suspend fun toggleItem(event: Event.OnToggleItem) {
        savePreferenceUseCase(event.toggleItem.key, event.isChecked)
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

    private suspend fun sliderItem(event: Event.OnSliderItem) {
        _uiState.update { oldState ->
            oldState.copy(
                sliderPreferences = oldState.sliderPreferences.map {
                    if (event.sliderItem == it) {
                        it.copy(
                            sliderValue = event.value
                        )
                    } else {
                        it
                    }
                }
            )
        }

        if (event.sliderItem.key == ManageUiSAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE) {
            manageUiSAppearanceUseCase(
                ManageUiSAppearanceUseCase.UiAppearance.entries.firstOrNull {
                    it.optionIndex == event.value.toInt()
                } ?: ManageUiSAppearanceUseCase.UiAppearance.SYSTEM
            )
        } else {
            savePreferenceUseCase(event.sliderItem.key, event.value.toInt())
        }
    }

    @Composable
    private fun mapUiAppearance(sliderValue: Float): String {
        val uiAppearance =
            ManageUiSAppearanceUseCase.UiAppearance.entries.firstOrNull {
                it.optionIndex == sliderValue.toInt()
            } ?: ManageUiSAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE

        return when (uiAppearance) {
            ManageUiSAppearanceUseCase.UiAppearance.SYSTEM -> stringResource(
                Res.string.settings_preference_ui_appearance_system
            )

            ManageUiSAppearanceUseCase.UiAppearance.DARK -> stringResource(
                Res.string.settings_preference_ui_appearance_dark
            )

            ManageUiSAppearanceUseCase.UiAppearance.LIGHT -> stringResource(
                Res.string.settings_preference_ui_appearance_light
            )
        }
    }

    sealed interface Event {
        data class OnToggleItem(
            val toggleItem: ToggleItem,
            val isChecked: Boolean,
        ) : Event

        data class OnSliderItem(
            val sliderItem: SliderItem,
            val value: Float,
        ) : Event
    }

    data class UiState(
        val togglePreferences: List<ToggleItem> = emptyList(),
        val sliderPreferences: List<SliderItem> = emptyList(),
    )

    data class ToggleItem(
        val isChecked: Boolean,
        val label: StringResource,
        val key: String,
    )

    data class SliderItem(
        val key: String,
        val sliderValue: Float,
        val label: StringResource,
        val steps: Int,
        val minimum: Float = 0f,
        val maximum: Float = 100f,
        val labelValue: @Composable (Float) -> String = { sliderValue.toInt().toString() }
    )

    companion object {
        const val HIDE_WELCOME_SCREEN_PREF_KEY = "HIDE_WELCOME_SCREEN"
        const val TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY = "TRACK_SCRIPTS_FILE_DELAY_SLIDER"
    }
}