package de.joz.appcommander.ui.edit

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.navigation.NavController
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptByKeyUseCase
import de.joz.appcommander.domain.script.RemoveUserScriptUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
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
	private val removeUserScriptUseCaseMock = RemoveUserScriptUseCase(scriptsRepository = scriptsRepositoryMock)

	private val screenshotVerifier =
		ScreenshotVerifier(
			testClass = javaClass,
		)

	@Test
	fun `show default ui when no script was selected for editing before`() {
		runComposeUiTest {
			setupData()
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
			val testScript =
				ScriptsRepository.Script(
					label = "Toggle Dark Mode On and Off",
					platform = ScriptsRepository.Platform.ANDROID,
					scripts = listOf("adb shell cmd uimode night yes", "sleep 3", "adb shell cmd uimode night no"),
				)
			setupData(
				script = testScript,
			)
			setTestContent(scriptKey = testScript.hashCode())
			waitUntilAtLeastOneExists(hasText(text = testScript.label))

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

			onNodeWithText(text = "Save script").performClick()

			verify {
				scriptsRepositoryMock.updateScript(
					script = expectedScript,
					oldScript = baseScript,
				)
			}
		}
	}

	@Test
	fun `run all scripts when run button is clicked`() {
		runComposeUiTest {
			val removeScript =
				ScriptsRepository.Script(
					label = "Toggle Dark Mode On and Off",
					platform = ScriptsRepository.Platform.ANDROID,
					scripts = listOf("adb shell cmd uimode night yes", "adb shell cmd uimode night no"),
				)
			coEvery { executeScriptUseCaseMock(any(), any()) } returns ExecuteScriptUseCase.Result.Success("")

			setupData(script = removeScript)
			setTestContent(scriptKey = removeScript.hashCode())

			onNodeWithContentDescription(label = "Execute all scripts").performClick()

			coVerify { executeScriptUseCaseMock(script = removeScript, selectedDevice = "TODO") }
		}
	}

	@Test
	fun `run one script when run button is clicked`() {
		runComposeUiTest {
			val removeScript =
				ScriptsRepository.Script(
					label = "Test",
					platform = ScriptsRepository.Platform.DESKTOP,
					scripts = listOf("echo Hello", "echo world!"),
				)
			coEvery { executeScriptUseCaseMock(any(), any()) } returns ExecuteScriptUseCase.Result.Success("")

			setupData(script = removeScript)
			setTestContent(scriptKey = removeScript.hashCode())

			onAllNodes(hasContentDescription("Execute script text")).apply {
				get(0).performClick()
				get(1).performClick()
			}

			coVerify {
				executeScriptUseCaseMock(
					script =
						ScriptsRepository.Script(
							label = "Test",
							scripts = listOf("echo Hello"),
							platform = ScriptsRepository.Platform.DESKTOP,
						),
					selectedDevice = "TODO",
				)
				executeScriptUseCaseMock(
					script =
						ScriptsRepository.Script(
							label = "Test",
							scripts = listOf("echo world!"),
							platform = ScriptsRepository.Platform.DESKTOP,
						),
					selectedDevice = "TODO",
				)
			}
		}
	}

	@Test
	fun `delete script when delete button is clicked and confirmation approved`() {
		runComposeUiTest {
			setupData()
			setTestContent()

			onNodeWithText(text = "Remove script").performClick()
			onNodeWithText(text = "Yes").performClick()

			verify { scriptsRepositoryMock.removeScript(any()) }
		}
	}

	@Test
	fun `delete script not when delete button is clicked but confirmation aborted`() {
		runComposeUiTest {
			setupData()
			setTestContent()

			onNodeWithText(text = "Remove script").performClick()
			onNodeWithText(text = "No").performClick()

			verify(exactly = 0) { scriptsRepositoryMock.removeScript(any()) }
		}
	}

	@Test
	fun `close screen when back button is clicked`() {
		runComposeUiTest {
			setupData()
			setTestContent()

			onNodeWithTag(testTag = "back_button").performClick()

			verify { navControllerMock.navigateUp() }
		}
	}

	@Test
	fun `close screen when close button is clicked`() {
		runComposeUiTest {
			setupData()
			setTestContent()

			onNodeWithText(text = "Close").performClick()

			verify { navControllerMock.navigateUp() }
		}
	}

	private fun setupData(script: ScriptsRepository.Script? = null) {
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
