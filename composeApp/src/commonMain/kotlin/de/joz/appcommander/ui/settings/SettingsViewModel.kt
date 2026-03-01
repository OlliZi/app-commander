package de.joz.appcommander.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.joz.appcommander.MainDispatcher
import de.joz.appcommander.domain.ManageUiAppearanceUseCase
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.settings_preference_show_filter_section
import de.joz.appcommander.resources.settings_preference_show_logging_section
import de.joz.appcommander.resources.settings_preference_show_terminal_section
import de.joz.appcommander.resources.settings_preference_show_welcome_screen
import de.joz.appcommander.resources.settings_preference_track_scripts_file_delay_slider_label
import de.joz.appcommander.resources.settings_preference_ui_appearance_dark
import de.joz.appcommander.resources.settings_preference_ui_appearance_label
import de.joz.appcommander.resources.settings_preference_ui_appearance_light
import de.joz.appcommander.resources.settings_preference_ui_appearance_system
import de.joz.appcommander.ui.misc.UnidirectionalDataFlowViewModel
import de.joz.appcommander.ui.model.ToolSection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class SettingsViewModel(
	@InjectedParam private val navController: NavController,
	private val getPreferenceUseCase: GetPreferenceUseCase,
	private val savePreferenceUseCase: SavePreferenceUseCase,
	private val manageUiAppearanceUseCase: ManageUiAppearanceUseCase,
	@MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) : ViewModel(),
	UnidirectionalDataFlowViewModel<SettingsViewModel.UiState, SettingsViewModel.Event> {
	private val _uiState = MutableStateFlow(UiState())
	override val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch(mainDispatcher) {
			_uiState.update { oldState ->
				oldState.copy(
					togglePreferences =
						listOf(
							createToggleItem(
								label = Res.string.settings_preference_show_welcome_screen,
								key = HIDE_WELCOME_SCREEN_PREF_KEY,
								defaultValue = false,
							),
							createToggleItem(
								label = Res.string.settings_preference_show_filter_section,
								key = ToolSection.FILTER.name,
								defaultValue = ToolSection.FILTER.isDefaultActive,
							),
							createToggleItem(
								label = Res.string.settings_preference_show_terminal_section,
								key = ToolSection.TERMINAL.name,
								defaultValue = ToolSection.TERMINAL.isDefaultActive,
							),
							createToggleItem(
								label = Res.string.settings_preference_show_logging_section,
								key = ToolSection.LOGGING.name,
								defaultValue = ToolSection.LOGGING.isDefaultActive,
							),
						),
					sliderPreferences =
						listOf(
							getPreferenceUseCase
								.get(
									key = TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY,
									defaultValue = 5,
								).let { sliderValue ->
									SliderItem(
										maximum = 10f,
										minimum = 1f,
										steps = 8,
										sliderValue = sliderValue.toFloat(),
										labelValue = LabelValue.IntRes(sliderValue),
										label = Res.string.settings_preference_track_scripts_file_delay_slider_label,
										key = TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY,
									)
								},
							getPreferenceUseCase
								.get(
									key = ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
									defaultValue = ManageUiAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex,
								).toFloat()
								.let { mapUiAppearance ->
									SliderItem(
										maximum =
											ManageUiAppearanceUseCase.UiAppearance.entries
												.maxOf { it.optionIndex }
												.toFloat(),
										minimum =
											ManageUiAppearanceUseCase.UiAppearance.entries
												.minOf { it.optionIndex }
												.toFloat(),
										steps = 1,
										sliderValue = mapUiAppearance,
										labelValue =
											LabelValue.StringRes(
												mapUiAppearance(mapUiAppearance),
											),
										label = Res.string.settings_preference_ui_appearance_label,
										key = ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
									)
								},
						),
				)
			}
		}
	}

	override fun onEvent(event: Event) {
		viewModelScope.launch(mainDispatcher) {
			when (event) {
				is Event.OnToggleItem -> toggleItem(event)
				is Event.OnSliderItem -> sliderItem(event)
				is Event.OnNavigateBack -> onNavigateBack()
			}
		}
	}

	private fun onNavigateBack() {
		navController.navigateUp()
	}

	private suspend fun toggleItem(event: Event.OnToggleItem) {
		savePreferenceUseCase(event.toggleItem.key, event.isChecked)
		_uiState.update { oldState ->
			oldState.copy(
				togglePreferences =
					oldState.togglePreferences.map {
						if (event.toggleItem == it) {
							it.copy(isChecked = event.isChecked)
						} else {
							it
						}
					},
			)
		}
	}

	private suspend fun sliderItem(event: Event.OnSliderItem) {
		_uiState.update { oldState ->
			oldState.copy(
				sliderPreferences =
					oldState.sliderPreferences.map {
						if (event.sliderItem.key == it.key) {
							it.copy(
								sliderValue = event.value,
								labelValue =
									when (event.sliderItem.labelValue) {
										is LabelValue.IntRes -> {
											LabelValue.IntRes(event.value.toInt())
										}

										is LabelValue.StringRes -> {
											LabelValue.StringRes(
												mapUiAppearance(event.value),
											)
										}
									},
							)
						} else {
							it
						}
					},
			)
		}

		if (event.sliderItem.key == ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE) {
			val uiAppearance =
				ManageUiAppearanceUseCase.UiAppearance.entries.firstOrNull {
					it.optionIndex == event.value.toInt()
				} ?: ManageUiAppearanceUseCase.UiAppearance.SYSTEM
			manageUiAppearanceUseCase(uiAppearance)
		} else {
			savePreferenceUseCase(event.sliderItem.key, event.value.toInt())
		}
	}

	private fun mapUiAppearance(sliderValue: Float): StringResource {
		val uiAppearance =
			ManageUiAppearanceUseCase.UiAppearance.entries.firstOrNull {
				it.optionIndex == sliderValue.toInt()
			} ?: ManageUiAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE

		return when (uiAppearance) {
			ManageUiAppearanceUseCase.UiAppearance.SYSTEM -> {
				Res.string.settings_preference_ui_appearance_system
			}

			ManageUiAppearanceUseCase.UiAppearance.DARK -> {
				Res.string.settings_preference_ui_appearance_dark
			}

			ManageUiAppearanceUseCase.UiAppearance.LIGHT -> {
				Res.string.settings_preference_ui_appearance_light
			}
		}
	}

	private suspend fun createToggleItem(
		label: StringResource,
		key: String,
		defaultValue: Boolean = false,
	) = ToggleItem(
		label = label,
		key = key,
		isChecked =
			getPreferenceUseCase.get(
				key = key,
				defaultValue = defaultValue,
			),
	)

	sealed interface Event {
		data object OnNavigateBack : Event

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
		val labelValue: LabelValue,
		val steps: Int,
		val minimum: Float = 0f,
		val maximum: Float = 100f,
	)

	sealed interface LabelValue {
		data class StringRes(
			val value: StringResource,
		) : LabelValue

		data class IntRes(
			val value: Int,
		) : LabelValue
	}

	companion object {
		const val HIDE_WELCOME_SCREEN_PREF_KEY = "HIDE_WELCOME_SCREEN"
		const val TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY = "TRACK_SCRIPTS_FILE_DELAY_SLIDER"
	}
}
