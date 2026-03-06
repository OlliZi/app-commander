package de.joz.appcommander.ui.edit

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.NavController
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptByKeyUseCase
import de.joz.appcommander.domain.script.RemoveUserScriptUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class EditScriptScreenTest {
	private val navControllerMock: NavController = mockk(relaxed = true)
	private val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = true)
	private val getScriptIdUseCaseMock: GetScriptIdUseCase = mockk(relaxed = true)
	private val executeScriptUseCaseMock: ExecuteScriptUseCase = mockk(relaxed = true)
	private val saveUserScriptUseCaseMock: SaveUserScriptUseCase = mockk(relaxed = true)
	private val removeUserScriptUseCaseMock: RemoveUserScriptUseCase = mockk(relaxed = true)

	private val screenshotVerifier =
		ScreenshotVerifier(
			testClass = javaClass,
		)

	@Test
	fun `show default ui when no script was selected for editing before`() {
		runComposeUiTest {
			setupData(script = null)
			setTestContent()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "default_edit",
			)
		}
	}

	@Test
	fun `show ui with selected script when a script was selected for editing before`() {
		runComposeUiTest {
			setupData(
				script =
					ScriptsRepository.Script(
						label = "Toggle Dark Mode On and Off",
						platform = ScriptsRepository.Platform.ANDROID,
						scripts = listOf("adb shell cmd uimode night yes", "sleep 3", "adb shell cmd uimode night no"),
					),
			)
			setTestContent()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "edit_script_ui",
			)
		}
	}

	private fun setupData(script: ScriptsRepository.Script?) {
		every { getUserScriptByKeyUseCaseMock.invoke(any()) } returns script
	}

	private fun ComposeUiTest.setTestContent() {
		setContent {
			AppCommanderTheme(
				darkTheme = true,
				content = {
					EditScriptScreen(
						viewModel =
							EditScriptViewModel(
								navController = navControllerMock,
								getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
								getScriptIdUseCase = getScriptIdUseCaseMock,
								executeScriptUseCase = executeScriptUseCaseMock,
								saveUserScriptUseCase = saveUserScriptUseCaseMock,
								removeUserScriptUseCase = removeUserScriptUseCaseMock,
								mainDispatcher = Dispatchers.Unconfined,
								ioDispatcher = Dispatchers.Unconfined,
								scriptKey = 1,
							),
					)
				},
			)
		}
	}
}
