package de.joz.appcommander.ui.settings

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.NavController
import de.joz.appcommander.domain.ManageUiAppearanceUseCase
import de.joz.appcommander.domain.ManageUiAppearanceUseCase.Companion.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.preference.PreferencesRepository
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.settings_preference_ui_appearance_light
import de.joz.appcommander.ui.settings.SettingsViewModel.Companion.HIDE_WELCOME_SCREEN_PREF_KEY
import de.joz.appcommander.ui.settings.SettingsViewModel.Companion.TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY
import de.joz.appcommander.ui.theme.AppCommanderTheme
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SettingsScreenTest {
	private val navControllerMock: NavController = mockk()
	private val getPreferenceUseCaseMock: GetPreferenceUseCase = mockk(relaxed = true)
	private val preferencesRepositoryMock: PreferencesRepository = mockk(relaxed = true)
	private val savePreferenceUseCaseMock =
		SavePreferenceUseCase(
			preferencesRepository = preferencesRepositoryMock,
		)
	private val manageUiAppearanceUseCase =
		ManageUiAppearanceUseCase(
			preferencesRepository = preferencesRepositoryMock,
		)
	private val screenshotVerifier =
		ScreenshotVerifier(
			testClass = javaClass,
		)

	private lateinit var viewModel: SettingsViewModel

	@BeforeTest
	fun setUp() {
		coEvery {
			getPreferenceUseCaseMock.get(any<String>(), any<Boolean>())
		} returns false

		coEvery {
			getPreferenceUseCaseMock.get(TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY, any<Int>())
		} returns 1

		coEvery {
			getPreferenceUseCaseMock.get(STORE_KEY_FOR_SYSTEM_UI_APPEARANCE, any<Int>())
		} returns 0

		viewModel =
			SettingsViewModel(
				navController = navControllerMock,
				getPreferenceUseCase = getPreferenceUseCaseMock,
				savePreferenceUseCase = savePreferenceUseCaseMock,
				manageUiAppearanceUseCase = manageUiAppearanceUseCase,
				mainDispatcher = Dispatchers.Unconfined,
			)
	}

	@Test
	fun `should show default label when default settings are applied`() {
		runComposeUiTest {
			setTestContent()

			onNodeWithText("Settings").assertIsDisplayed()
			onNodeWithText("Hide welcome screen on startup.").assertIsDisplayed()
			onNodeWithText("Automatically refresh scripts list all 1 seconds.").assertIsDisplayed()
			onNodeWithText("Selected ui appearance: 'System'.").assertIsDisplayed()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "default_label",
			)
		}
	}

	@Test
	fun `should show changes when settings are applied`() {
		runComposeUiTest {
			setTestContent(
				uiState =
					SettingsViewModel.UiState(
						togglePreferences =
							viewModel.uiState.value.togglePreferences.map {
								it.copy(
									isChecked = true,
								)
							},
						sliderPreferences =
							viewModel.uiState.value.sliderPreferences.map {
								it.copy(
									sliderValue = it.maximum,
									labelValue =
										when (it.labelValue) {
											is SettingsViewModel.LabelValue.IntRes -> {
												SettingsViewModel.LabelValue.IntRes(it.maximum.toInt())
											}

											is SettingsViewModel.LabelValue.StringRes -> {
												SettingsViewModel.LabelValue.StringRes(Res.string.settings_preference_ui_appearance_light)
											}
										},
								)
							},
					),
			)

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "changed_label",
			)
			onNodeWithText("Settings").assertIsDisplayed()
			onNodeWithText("Hide welcome screen on startup.").assertIsDisplayed()
			onNodeWithText("Automatically refresh scripts list all 10 seconds.").assertIsDisplayed()
			onNodeWithText("Selected ui appearance: 'Light'.").assertIsDisplayed()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "changed_label",
			)
		}
	}

	@Test
	fun `should toggle hide welcome screen on startup when toggled`() {
		runComposeUiTest {
			setTestContent()

			onNodeWithText("Hide welcome screen on startup.").assertIsDisplayed().performClick()

			coVerify { savePreferenceUseCaseMock.invoke(HIDE_WELCOME_SCREEN_PREF_KEY, true) }
		}
	}

	@Test
	fun `should change ui appearance when slider is moved`() {
		runComposeUiTest {
			setTestContent()

			onNodeWithTag(STORE_KEY_FOR_SYSTEM_UI_APPEARANCE)
				.assertIsDisplayed()
				.performClick()

			coVerify {
				preferencesRepositoryMock.store(
					key = STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
					value = ManageUiAppearanceUseCase.UiAppearance.DARK.optionIndex,
				)
			}
		}
	}

	@Test
	fun `should change track scripts file delay when slider is moved`() {
		runComposeUiTest {
			setTestContent()

			onNodeWithTag(TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY)
				.assertIsDisplayed()
				.performClick()

			coVerify {
				preferencesRepositoryMock.store(
					key = TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY,
					value = 5,
				)
			}
		}
	}

	private fun ComposeUiTest.setTestContent(uiState: SettingsViewModel.UiState? = null) {
		setContent {
			AppCommanderTheme(
				darkTheme = true,
				content = {
					SettingsScreen(
						viewModel = viewModel,
						uiState = uiState ?: viewModel.uiState.value,
					)
				},
			)
		}
	}
}
