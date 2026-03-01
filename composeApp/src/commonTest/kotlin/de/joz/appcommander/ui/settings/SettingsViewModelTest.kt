package de.joz.appcommander.ui.settings

import androidx.navigation.NavController
import de.joz.appcommander.domain.misc.ManageUiAppearanceUseCase
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
import de.joz.appcommander.ui.model.ToolSection
import de.joz.appcommander.ui.settings.SettingsViewModel.LabelValue.StringRes
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
	private val savePreferenceUseCaseMock: SavePreferenceUseCase = mockk(relaxed = true)
	private val getPreferenceUseCaseMock: GetPreferenceUseCase = mockk()
	private val manageUiAppearanceUseCaseMock: ManageUiAppearanceUseCase = mockk(relaxed = true)
	private val navControllerMock: NavController = mockk(relaxed = true)

	@BeforeTest
	fun setUp() {
		coEvery {
			getPreferenceUseCaseMock.get(any<String>(), any<Boolean>())
		} returns false
		coEvery {
			getPreferenceUseCaseMock.get(any<String>(), any<Int>())
		} returns 0
	}

	@Test
	fun `should return none empty key for HIDE_WELCOME_SCREEN_PREF_KEY`() {
		assertEquals(
			"HIDE_WELCOME_SCREEN",
			SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY,
		)
	}

	@Test
	fun `should return none empty key for TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY`() {
		assertEquals(
			"TRACK_SCRIPTS_FILE_DELAY_SLIDER",
			SettingsViewModel.TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY,
		)
	}

	@Test
	fun `should return default state when viewmodel is initialized`() =
		runTest {
			val viewModel = createViewModel()
			runCurrent()

			val uiState = viewModel.uiState.value

			assertEquals(4, uiState.togglePreferences.size)
			assertEquals(2, uiState.sliderPreferences.size)

			assertEquals(
				SettingsViewModel.ToggleItem(
					isChecked = false,
					label = Res.string.settings_preference_show_welcome_screen,
					key = SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY,
				),
				uiState.togglePreferences[0],
			)

			assertEquals(
				SettingsViewModel.ToggleItem(
					isChecked = false,
					label = Res.string.settings_preference_show_filter_section,
					key = ToolSection.FILTER.name,
				),
				uiState.togglePreferences[1],
			)

			assertEquals(
				SettingsViewModel.ToggleItem(
					isChecked = false,
					label = Res.string.settings_preference_show_terminal_section,
					key = ToolSection.TERMINAL.name,
				),
				uiState.togglePreferences[2],
			)

			assertEquals(
				SettingsViewModel.ToggleItem(
					isChecked = false,
					label = Res.string.settings_preference_show_logging_section,
					key = ToolSection.LOGGING.name,
				),
				uiState.togglePreferences[3],
			)

			assertEquals(
				SettingsViewModel.SliderItem(
					maximum = 10f,
					minimum = 1f,
					steps = 8,
					sliderValue = 0f,
					label = Res.string.settings_preference_track_scripts_file_delay_slider_label,
					key = SettingsViewModel.TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY,
					labelValue = SettingsViewModel.LabelValue.IntRes(0),
				),
				uiState.sliderPreferences[0],
			)

			assertEquals(
				SettingsViewModel.SliderItem(
					maximum = 2f,
					minimum = 0f,
					steps = 1,
					sliderValue = 0f,
					label = Res.string.settings_preference_ui_appearance_label,
					key = ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
					labelValue = StringRes(Res.string.settings_preference_ui_appearance_system),
				),
				uiState.sliderPreferences[1],
			)
		}

	@Test
	fun `should navigate back when event 'OnNavigateBack' is fired`() =
		runTest {
			val viewModel = createViewModel()
			runCurrent()

			viewModel.onEvent(event = SettingsViewModel.Event.OnNavigateBack)
			runCurrent()

			verify {
				navControllerMock.navigateUp()
			}
		}

	@Test
	fun `should toggle item when event 'OnToggleItem' is fired`() =
		runTest {
			val viewModel = createViewModel()
			runCurrent()

			viewModel.uiState.value.togglePreferences.forEach {
				viewModel.onEvent(
					event =
						SettingsViewModel.Event.OnToggleItem(
							isChecked = true,
							toggleItem = it,
						),
				)
				runCurrent()
			}
			coVerify(exactly = viewModel.uiState.value.togglePreferences.size) {
				savePreferenceUseCaseMock.invoke(any(), true)
			}
			assertTrue(
				viewModel.uiState.value.togglePreferences
					.all { it.isChecked },
			)

			viewModel.uiState.value.togglePreferences.forEach {
				viewModel.onEvent(
					event =
						SettingsViewModel.Event.OnToggleItem(
							isChecked = false,
							toggleItem = it,
						),
				)
				runCurrent()
			}
			coVerify(exactly = viewModel.uiState.value.togglePreferences.size) {
				savePreferenceUseCaseMock.invoke(any(), false)
			}
			assertFalse(
				viewModel.uiState.value.togglePreferences
					.all { it.isChecked },
			)
		}

	@Test
	fun `should change slider item when event 'OnSliderItem' is fired`() =
		runTest {
			val viewModel = createViewModel()
			runCurrent()

			viewModel.uiState.value.sliderPreferences.forEach {
				viewModel.onEvent(
					event =
						SettingsViewModel.Event.OnSliderItem(
							value = it.maximum,
							sliderItem = it,
						),
				)
				runCurrent()

				if (it.key == ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE) {
					coVerify {
						manageUiAppearanceUseCaseMock.invoke(any())
					}
				} else {
					coVerify {
						savePreferenceUseCaseMock.invoke(any(), any<Int>())
					}
				}
			}
			assertTrue(
				viewModel.uiState.value.sliderPreferences
					.all { it.maximum == it.sliderValue },
			)

			viewModel.uiState.value.sliderPreferences.forEach {
				viewModel.onEvent(
					event =
						SettingsViewModel.Event.OnSliderItem(
							value = it.minimum,
							sliderItem = it,
						),
				)
				runCurrent()

				if (it.key == ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE) {
					coVerify {
						manageUiAppearanceUseCaseMock.invoke(any())
					}
				} else {
					coVerify {
						savePreferenceUseCaseMock.invoke(any(), any<Int>())
					}
				}
			}
			assertTrue(
				viewModel.uiState.value.sliderPreferences
					.all { it.minimum == it.sliderValue },
			)
		}

	@Test
	fun `should change ui appearance when event 'OnSliderItem' of type 'ui appearance' is fired`() =
		runTest {
			val viewModel = createViewModel()
			runCurrent()

			val uiAppearanceSlider =
				viewModel.uiState.value.sliderPreferences.find {
					it.key == ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE
				}
			assertNotNull(uiAppearanceSlider)

			(uiAppearanceSlider.minimum.toInt()..uiAppearanceSlider.maximum.toInt()).forEach { sliderValue ->
				viewModel.onEvent(
					event =
						SettingsViewModel.Event.OnSliderItem(
							value = sliderValue.toFloat(),
							sliderItem = uiAppearanceSlider,
						),
				)
				runCurrent()

				val slider =
					viewModel.uiState.value.sliderPreferences.find {
						it.key == ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE
					}
				assertEquals(sliderValue.toFloat(), slider?.sliderValue)

				val expectedUiAppearance =
					ManageUiAppearanceUseCase.UiAppearance.entries.firstOrNull {
						it.optionIndex == sliderValue
					}

				assertEquals(
					when (expectedUiAppearance) {
						ManageUiAppearanceUseCase.UiAppearance.SYSTEM -> {
							StringRes(
								Res.string.settings_preference_ui_appearance_system,
							)
						}

						ManageUiAppearanceUseCase.UiAppearance.DARK -> {
							StringRes(
								Res.string.settings_preference_ui_appearance_dark,
							)
						}

						ManageUiAppearanceUseCase.UiAppearance.LIGHT -> {
							StringRes(
								Res.string.settings_preference_ui_appearance_light,
							)
						}

						null -> {
							throw IllegalStateException("Fix test.")
						}
					},
					slider?.labelValue,
				)

				coVerify {
					manageUiAppearanceUseCaseMock.invoke(expectedUiAppearance)
				}
			}
		}

	private fun createViewModel(): SettingsViewModel =
		SettingsViewModel(
			savePreferenceUseCase = savePreferenceUseCaseMock,
			getPreferenceUseCase = getPreferenceUseCaseMock,
			manageUiAppearanceUseCase = manageUiAppearanceUseCaseMock,
			navController = navControllerMock,
			mainDispatcher = Dispatchers.Unconfined,
		)
}
