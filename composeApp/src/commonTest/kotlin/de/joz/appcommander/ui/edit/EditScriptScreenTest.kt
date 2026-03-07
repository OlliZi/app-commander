package de.joz.appcommander.ui.edit

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.NavController
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptByKeyUseCase
import de.joz.appcommander.domain.script.RemoveUserScriptUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.edit_action_save
import de.joz.appcommander.ui.theme.AppCommanderTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.getString
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class EditScriptScreenTest {
	private val navControllerMock: NavController = mockk(relaxed = true)
	private val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
	private val getScriptIdUseCaseMock: GetScriptIdUseCase = mockk(relaxed = true)
	private val getUserScriptByKeyUseCaseMock =
		GetUserScriptByKeyUseCase(
			scriptsRepository = scriptsRepositoryMock,
			getScriptIdUseCase = getScriptIdUseCaseMock,
		)
	private val executeScriptUseCaseMock: ExecuteScriptUseCase = mockk(relaxed = true)
	private val saveUserScriptUseCaseMock =
		SaveUserScriptUseCase(
			scriptsRepository = scriptsRepositoryMock,
			getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
		)
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

	@Test
	fun `change script when a script was selected for editing`() {
		runComposeUiTest {
			val baseScript =
				ScriptsRepository.Script(
					label = "Toggle Dark Mode On and Off",
					platform = ScriptsRepository.Platform.ANDROID,
					scripts = listOf("adb shell cmd uimode night yes", "sleep 3", "adb shell cmd uimode night no"),
				)
			val expectedScript =
				ScriptsRepository.Script(
					label = "new script name",
					platform = ScriptsRepository.Platform.DESKTOP,
					scripts = listOf("new script 1", "sleep 0", "new script 2"),
				)
			setupData(
				script = baseScript,
			)
			setTestContent(scriptKey = baseScript.hashCode())

			onNodeWithTag(testTag = "text_field_simple_text").apply {
				performTextClearance()
				performTextInput("new script name")
			}
			onAllNodes(hasTestTag("text_field_script_input"))[0].apply {
				performTextClearance()
				performTextInput("new script 1")
			}
			onAllNodes(hasTestTag("text_field_script_input"))[1].apply {
				performTextClearance()
				performTextInput("sleep 0")
			}
			onAllNodes(hasTestTag("text_field_script_input"))[2].apply {
				performTextClearance()
				performTextInput("new script 2")
			}
			onNodeWithText(text = ScriptsRepository.Platform.DESKTOP.label).performClick()

			onNodeWithText(text = getString(Res.string.edit_action_save)).performClick()

			verify {
				scriptsRepositoryMock.updateScript(
					script = expectedScript,
					oldScript = baseScript,
				)
			}
		}
	}

	private fun setupData(script: ScriptsRepository.Script?) {
		every { getScriptIdUseCaseMock.invoke(any()) } returns (script?.hashCode() ?: 0)
		every { scriptsRepositoryMock.getScripts() } returns
			ScriptsRepository.JsonParseResult(
				scripts = if (script != null) listOf(script) else emptyList(),
				parsingMetaData = null,
			)
	}

	private fun ComposeUiTest.setTestContent(scriptKey: Int? = null) {
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
								scriptKey = scriptKey,
							),
					)
				},
			)
		}
	}
}
