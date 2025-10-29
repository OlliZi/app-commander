package de.joz.appcommander.ui.settings

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.NavController
import de.joz.appcommander.domain.GetPreferenceUseCase
import de.joz.appcommander.domain.ManageUiAppearanceUseCase
import de.joz.appcommander.domain.SavePreferenceUseCase
import de.joz.appcommander.helper.screenshot.ScreenshotVerifier
import de.joz.appcommander.ui.settings.SettingsViewModel.Companion.TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY
import de.joz.appcommander.ui.theme.AppCommanderTheme
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SettingsScreenTest {
	private val navControllerMock: NavController = mockk()
	private val getPreferenceUseCaseMock: GetPreferenceUseCase = mockk()
	private val savePreferenceUseCaseMock: SavePreferenceUseCase = mockk()
	private val manageUiAppearanceUseCaseMock: ManageUiAppearanceUseCase = mockk()
	private val screenshotVerifier =
		ScreenshotVerifier(
			testClass = javaClass,
		)

	@Test
	fun `should show default label when default settings are applied`() {
		runComposeUiTest {
			setTestContent()

			onNodeWithText("Settings").assertIsDisplayed()
			onNodeWithText("Hide welcome screen on startup.").assertIsDisplayed()
			onNodeWithText("Automatically refresh scripts list all 5 seconds.").assertIsDisplayed()
			onNodeWithText("Selected ui appearance: 'System'.").assertIsDisplayed()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "default_label",
			)
		}
	}

	private fun ComposeUiTest.setTestContent() {
		coEvery {
			getPreferenceUseCaseMock.get(any<String>(), any<Boolean>())
		} returns false

		coEvery {
			getPreferenceUseCaseMock.get(TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY, any<Int>())
		} returns 5

		coEvery {
			getPreferenceUseCaseMock.get(ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE, any<Int>())
		} returns 0

		val viewModel =
			SettingsViewModel(
				navController = navControllerMock,
				getPreferenceUseCase = getPreferenceUseCaseMock,
				savePreferenceUseCase = savePreferenceUseCaseMock,
				manageUiAppearanceUseCase = manageUiAppearanceUseCaseMock,
				mainDispatcher = Dispatchers.Unconfined,
			)
		setContent {
			AppCommanderTheme(
				darkTheme = true,
				content = {
					SettingsScreen(
						viewModel = viewModel,
						uiState = viewModel.uiState.value,
					)
				},
			)
		}
	}
}
