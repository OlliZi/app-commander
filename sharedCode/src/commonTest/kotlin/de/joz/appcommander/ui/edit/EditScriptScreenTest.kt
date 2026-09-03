package de.joz.appcommander.ui.edit

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.navigation.NavController
import de.joz.appcommander.DependencyInjection
import de.joz.appcommander.domain.devices.GetDevicesUseCase
import de.joz.appcommander.domain.devices.ObserveDevicesUseCase
import de.joz.appcommander.domain.model.Device
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptByKeyUseCase
import de.joz.appcommander.domain.script.RemoveUserScriptUseCase
import de.joz.appcommander.domain.script.RunFileBackupUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.GetDevicesUseCaseMock
import de.joz.appcommander.helper.TestRuleApplier
import de.joz.appcommander.helper.screenshot.ScreenshotVerifier
import de.joz.appcommander.helper.toSubScripts
import de.joz.appcommander.ui.theme.AppCommanderTheme
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.koin.dsl.module
import org.koin.ksp.generated.*
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class EditScriptScreenTest :
	TestRuleApplier(),
	KoinTest {
	private val navControllerMock: NavController = mockk(relaxed = true)
	private val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
	private val getScriptIdUseCaseMock: GetScriptIdUseCase = mockk(relaxed = true)
	private val getUserScriptByKeyUseCaseMock = GetUserScriptByKeyUseCase(
		scriptsRepository = scriptsRepositoryMock,
		getScriptIdUseCase = getScriptIdUseCaseMock,
	)
	private val executeScriptUseCaseMock: ExecuteScriptUseCase = mockk(relaxed = true)
	private val runFileBackupUseCaseMock: RunFileBackupUseCase = mockk(relaxed = true)
	private val saveUserScriptUseCaseMock = SaveUserScriptUseCase(
		scriptsRepository = scriptsRepositoryMock,
		getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
		runFileBackupUseCase = runFileBackupUseCaseMock,
	)
	private val removeUserScriptUseCaseMock: RemoveUserScriptUseCase = mockk(relaxed = true)

	private val screenshotVerifier = ScreenshotVerifier(
		testClass = javaClass,
	)
	private val defaultTestDevices = listOf(
		Device(
			label = "emulator-5555",
			id = "1",
			isSelected = true,
		),
		Device(
			label = "emulator-5556",
			id = "2",
			isSelected = false,
		),
		Device(
			label = "Google Pixel 10",
			id = "3",
			isSelected = true,
		),
	)
	private var testDevices = defaultTestDevices
	private val getDevicesUseCaseMock = GetDevicesUseCaseMock {
		testDevices
	}

	@get:Rule
	val koinTestRule = KoinTestRule.create {
		modules(DependencyInjection().module)
		modules(
			module {
				single {
					mockk<ObserveDevicesUseCase>(relaxed = false) {
						every { this@mockk.invoke() } returns flowOf(testDevices)
					}
				}
				single<GetDevicesUseCase> { getDevicesUseCaseMock }
			},
		)
	}

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
	fun `show no connected device when platform is DESKTOP`() {
		runComposeUiTest {
			val testScript = ScriptsRepository.Script(
				label = "bar",
				platform = ScriptsRepository.Platform.DESKTOP,
				scripts = listOf("foo").toSubScripts(),
			)
			setupData(
				script = testScript,
			)
			setTestContent(scriptKey = testScript.hashCode())

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "no_conected_devices_desktop",
			)
		}
	}

	@Test
	fun `should toggle device selection when corresponding platform is selected`() {
		runComposeUiTest {
			val testScript = ScriptsRepository.Script(
				label = "bar",
				platform = ScriptsRepository.Platform.DESKTOP,
				scripts = listOf("foo").toSubScripts(),
			)
			setupData(
				script = testScript,
			)
			setTestContent(scriptKey = testScript.hashCode())

			onNodeWithText(text = "Refresh").assertDoesNotExist()

			ScriptsRepository.Platform.entries.forEach { platform ->
				onNodeWithText(text = platform.label).performClick()

				when (platform) {
					ScriptsRepository.Platform.ANDROID -> {
						onNodeWithText(text = "Refresh").assertIsDisplayed()
					}

					ScriptsRepository.Platform.IOS -> {
						onNodeWithText(text = "Refresh").assertIsDisplayed()
					}

					ScriptsRepository.Platform.DESKTOP -> {
						onNodeWithText(text = "Refresh").assertDoesNotExist()
					}
				}
			}
		}
	}

	@Test
	fun `show error messages in ui when script can saved but backup fails`() {
		runComposeUiTest {
			val testScript = ScriptsRepository.Script(
				label = "Toggle Dark Mode On and Off",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf(
					"adb shell cmd uimode night yes",
					"sleep 3",
					"adb shell cmd uimode night no",
				).toSubScripts(),
			)
			setupData(
				script = testScript,
			)
			setTestContent(scriptKey = testScript.hashCode())

			coEvery {
				runFileBackupUseCaseMock.invoke()
			} returns RunFileBackupUseCase.Result.CannotCreateBackupFile("cannot create backup file")
			coEvery {
				scriptsRepositoryMock.updateScript(any(), any())
			} returns ScriptsRepository.WriteScriptResult.Success(Unit)

			onNodeWithText(text = "Save script").performClick()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "error_messages_1",
			)
		}
	}

	@Test
	fun `show error messages in ui when script cannot saved but backup was successfully`() {
		runComposeUiTest {
			val testScript = ScriptsRepository.Script(
				label = "Toggle Dark Mode On and Off",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf(
					"adb shell cmd uimode night yes",
					"sleep 3",
					"adb shell cmd uimode night no",
				).toSubScripts(),
			)
			setupData(
				script = testScript,
			)
			setTestContent(scriptKey = testScript.hashCode())

			coEvery {
				runFileBackupUseCaseMock.invoke()
			} returns RunFileBackupUseCase.Result.Success
			coEvery {
				scriptsRepositoryMock.updateScript(any(), any())
			} returns ScriptsRepository.WriteScriptResult.SaveError("cannot save script")

			onNodeWithText(text = "Save script").performClick()
			waitUntilAtLeastOneExists(hasText(text = testScript.label))

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "error_messages_2",
			)
		}
	}

	@Test
	fun `show error messages in ui when script cannot saved and backup fails`() {
		runComposeUiTest {
			val testScript = ScriptsRepository.Script(
				label = "Toggle Dark Mode On and Off",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf(
					"adb shell cmd uimode night yes",
					"sleep 3",
					"adb shell cmd uimode night no",
				).toSubScripts(),
			)
			setupData(
				script = testScript,
			)
			setTestContent(scriptKey = testScript.hashCode())

			coEvery {
				runFileBackupUseCaseMock.invoke()
			} returns RunFileBackupUseCase.Result.UnknownError("unknown error")
			coEvery {
				scriptsRepositoryMock.updateScript(any(), any())
			} returns ScriptsRepository.WriteScriptResult.SaveError("cannot save script")

			onNodeWithText(text = "Save script").performClick()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "error_messages_3",
			)
		}
	}

	@Test
	fun `show ui with selected script when a script was selected for editing before`() {
		runComposeUiTest {
			val testScript = ScriptsRepository.Script(
				label = "Toggle Dark Mode On and Off",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf(
					"adb shell cmd uimode night yes",
					"sleep 3",
					"adb shell cmd uimode night no",
				).toSubScripts(),
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
			val baseScript = ScriptsRepository.Script(
				label = "Toggle Dark Mode On and Off",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf(
					"adb shell cmd uimode night yes",
					"sleep 3",
					"adb shell cmd uimode night no",
				).toSubScripts(),
			)
			val expectedScript = ScriptsRepository.Script(
				label = "new script name",
				comment = "new script comment",
				platform = ScriptsRepository.Platform.DESKTOP,
				scripts = listOf(
					ScriptsRepository.ScriptCode.CommentedScript(script = "new script 1", comment = "comment 1"),
					ScriptsRepository.ScriptCode.CommentedScript(script = "sleep 1", comment = "comment 2"),
					ScriptsRepository.ScriptCode.CommentedScript(
						script = "new script 2",
						comment = "comment 3",
					),
				),
			)
			setupData(
				script = baseScript,
			)
			setTestContent(scriptKey = baseScript.hashCode())

			onNodeWithTag(testTag = "text_field_simple_text_script").apply {
				performTextClearance()
				performTextInput("new script name")
			}
			onNodeWithTag(testTag = "text_field_simple_text_comment").apply {
				performTextClearance()
				performTextInput("new script comment")
			}

			val scriptInputs = onAllNodes(hasTestTag("text_field_script_input"))
			scriptInputs[0].apply {
				performTextClearance()
				performTextInput("new script 1")
			}
			scriptInputs[1].apply {
				performTextClearance()
				performTextInput("sleep 1")
			}
			scriptInputs[2].apply {
				performTextClearance()
				performTextInput("new script 2")
			}

			val moreInputs = onAllNodes(hasTestTag("show_more_button"))
			moreInputs[0].performClick()
			moreInputs[1].performClick()
			moreInputs[2].performClick()

			val commentInputs = onAllNodes(hasTestTag("text_field_script_comment"))
			commentInputs[0].apply {
				performTextClearance()
				performTextInput("comment 1")
			}
			commentInputs[1].apply {
				performTextClearance()
				performTextInput("comment 2")
			}
			commentInputs[2].apply {
				performTextClearance()
				performTextInput("comment 3")
			}

			onNodeWithText(text = ScriptsRepository.Platform.DESKTOP.label).performScrollTo().performClick()

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
			testDevices = listOf(
				Device(
					id = "id",
					label = "test device",
					isSelected = true,
				),
			)
			val script = ScriptsRepository.Script(
				label = "",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf("adb shell cmd uimode night yes", "adb shell cmd uimode night no").toSubScripts(),
			)
			coEvery { executeScriptUseCaseMock(any(), any()) } returns ExecuteScriptUseCase.Result.Success("")

			setupData(script = script)
			setTestContent(scriptKey = script.hashCode())

			onNodeWithContentDescription(label = "Execute all scripts").performClick()

			coVerify { executeScriptUseCaseMock(script = script, selectedDevice = "id") }
		}
	}

	@Test
	fun `do not run all scripts when run button is clicked but there is no device selected`() {
		runComposeUiTest {
			testDevices = listOf(
				Device(
					id = "id",
					label = "test device",
					isSelected = false,
				),
				Device(
					id = "id 2",
					label = "test device 2",
					isSelected = false,
				),
			)
			val script = ScriptsRepository.Script(
				label = "",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf("adb shell cmd uimode night yes", "adb shell cmd uimode night no").toSubScripts(),
			)
			coEvery { executeScriptUseCaseMock(any(), any()) } returns ExecuteScriptUseCase.Result.Success("")

			setupData(script = script)
			setTestContent(scriptKey = script.hashCode())

			onNodeWithContentDescription(label = "Execute all scripts").assertIsNotEnabled()

			coVerify { executeScriptUseCaseMock wasNot called }
		}
	}

	@Test
	fun `always run all scripts when run button is clicked and the selected platform is Desktop`() {
		runComposeUiTest {
			testDevices = emptyList()
			val script = ScriptsRepository.Script(
				label = "",
				platform = ScriptsRepository.Platform.DESKTOP,
				scripts = listOf("foo bar").toSubScripts(),
			)
			coEvery { executeScriptUseCaseMock(any(), any()) } returns ExecuteScriptUseCase.Result.Success("")

			setupData(script = script)
			setTestContent(scriptKey = script.hashCode())

			onNodeWithContentDescription(label = "Execute all scripts").performClick()

			coVerify { executeScriptUseCaseMock(script = script, selectedDevice = "") }
		}
	}

	@Test
	fun `should refresh devices when refresh is clicked`() {
		runComposeUiTest {
			testDevices = listOf(
				Device(
					id = "id 1",
					label = "test device before refresh",
					isSelected = true,
				),
			)
			setupData()
			setTestContent()

			onNodeWithText("test device before refresh").isDisplayed()
			onNodeWithText("test device after refresh").assertDoesNotExist()

			testDevices = listOf(
				Device(
					id = "id 1",
					label = "test device before refresh",
					isSelected = true,
				),
				Device(
					id = "id 2",
					label = "test device after refresh",
					isSelected = true,
				),
			)
			onNodeWithText(text = "Refresh").performClick()

			onNodeWithText("test device before refresh").isDisplayed()
			onNodeWithText("test device after refresh").isDisplayed()
		}
	}

	@Test
	fun `remove script when remove script button for a script is clicked`() {
		runComposeUiTest {
			val removeScript = ScriptsRepository.Script(
				label = "",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf("script 1", "script 2").toSubScripts(),
			)
			coEvery { executeScriptUseCaseMock(any(), any()) } returns ExecuteScriptUseCase.Result.Success("")

			setupData(script = removeScript)
			setTestContent(scriptKey = removeScript.hashCode())

			onNodeWithText("script 1").isDisplayed()
			onNodeWithText("script 2").isDisplayed()

			onAllNodes(hasContentDescription("Remove script"))[0].apply {
				performClick()
			}

			onNodeWithText("script 1").assertDoesNotExist()
			onNodeWithText("script 2").isDisplayed()
		}
	}

	@Test
	fun `add a new script when add script button for a script is clicked`() {
		runComposeUiTest {
			val addScript = ScriptsRepository.Script(
				label = "",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf("script 1", "script 2").toSubScripts(),
			)
			coEvery { executeScriptUseCaseMock(any(), any()) } returns ExecuteScriptUseCase.Result.Success("")

			setupData(script = addScript)
			setTestContent(scriptKey = addScript.hashCode())

			onNodeWithText("script 1").isDisplayed()
			onNodeWithText("script 2").isDisplayed()
			onNodeWithText("<enter new script>").assertDoesNotExist()

			onAllNodes(hasContentDescription("Add script"))[0].apply {
				performClick()
			}

			onNodeWithText("script 1").isDisplayed()
			onNodeWithText("script 2").isDisplayed()
			onNodeWithText("<enter new script>").isDisplayed()
		}
	}

	@Test
	fun `run one script when run button is clicked`() {
		runComposeUiTest {
			val script = ScriptsRepository.Script(
				label = "Test",
				platform = ScriptsRepository.Platform.DESKTOP,
				scripts = listOf("echo Hello", "echo world!").toSubScripts(),
			)
			coEvery { executeScriptUseCaseMock(any(), any()) } returns ExecuteScriptUseCase.Result.Success("")

			setupData(script = script)
			setTestContent(scriptKey = script.hashCode())

			onAllNodes(hasTestTag("show_more_button")).apply {
				get(0).performClick()
				get(1).performClick()
			}

			onAllNodes(hasContentDescription("Execute script text")).apply {
				get(0).performClick()
				get(1).performClick()
			}

			coVerify {
				executeScriptUseCaseMock(
					script = ScriptsRepository.Script(
						label = "",
						scripts = listOf("echo Hello").toSubScripts(),
						platform = ScriptsRepository.Platform.DESKTOP,
					),
					selectedDevice = "",
				)
				executeScriptUseCaseMock(
					script = ScriptsRepository.Script(
						label = "",
						scripts = listOf("echo world!").toSubScripts(),
						platform = ScriptsRepository.Platform.DESKTOP,
					),
					selectedDevice = "",
				)
			}
		}
	}

	@Test
	fun `do not run one script when run button is clicked but there is no device selected`() {
		runComposeUiTest {
			testDevices = listOf(
				Device(id = "id 1", label = "test device", isSelected = false),
				Device(id = "id 2", label = "test device", isSelected = false),
			)
			val script = ScriptsRepository.Script(
				label = "Test",
				platform = ScriptsRepository.Platform.ANDROID,
				scripts = listOf("echo Hello", "echo world!").toSubScripts(),
			)
			coEvery { executeScriptUseCaseMock(any(), any()) } returns ExecuteScriptUseCase.Result.Success("")

			setupData(script = script)
			setTestContent(scriptKey = script.hashCode())

			onAllNodes(hasTestTag("show_more_button")).apply {
				get(0).performClick()
				get(1).performClick()
			}

			onAllNodes(hasContentDescription("Execute script text")).apply {
				get(0).assertIsNotEnabled()
				get(1).assertIsNotEnabled()

				get(0).performClick() // nothing should happen
				get(1).performClick() // nothing should happen
			}

			coVerify {
				executeScriptUseCaseMock wasNot called
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

			verify { removeUserScriptUseCaseMock.invoke(any()) }
		}
	}

	@Test
	fun `delete script not when delete button is clicked but confirmation aborted`() {
		runComposeUiTest {
			setupData()
			setTestContent()

			onNodeWithText(text = "Remove script").performClick()
			onNodeWithText(text = "No").performClick()

			verify(exactly = 0) { removeUserScriptUseCaseMock.invoke(any()) }
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

	@Test
	fun `show confirmation when close button is clicked and script was changed before`() {
		runComposeUiTest {
			setupData()
			setTestContent()

			onNodeWithTag(testTag = "text_field_simple_text_script").apply {
				performTextClearance()
				performTextInput("new script name")
			}

			onNodeWithText(text = "Close").performClick()

			onNodeWithText(text = "You have changes in your script. Are you sure you want to leave?").isDisplayed()
			onNodeWithText(text = "Yes").isDisplayed()
			onNodeWithText(text = "No").isDisplayed()

			onNodeWithText(text = "Yes").performClick()

			verify { navControllerMock.navigateUp() }
		}
	}

	@Test
	fun `show confirmation when back button is clicked and script was changed before`() {
		runComposeUiTest {
			setupData()
			setTestContent()

			onNodeWithText(text = ScriptsRepository.Platform.DESKTOP.label).performClick()

			onNodeWithText(text = "Close").performClick()

			onNodeWithText(text = "You have changes in your script. Are you sure you want to leave?").isDisplayed()
			onNodeWithText(text = "Yes").isDisplayed()
			onNodeWithText(text = "No").isDisplayed()

			onNodeWithText(text = "Yes").performClick()

			verify { navControllerMock.navigateUp() }
		}
	}

	private fun setupData(script: ScriptsRepository.Script? = null) {
		every { getScriptIdUseCaseMock.invoke(any()) } returns (script?.hashCode() ?: 0)
		every { scriptsRepositoryMock.getScripts() } returns ScriptsRepository.JsonParseResult(
			scripts = if (script != null) listOf(script) else emptyList(),
			parsingMetaData = null,
		)
	}

	private fun ComposeUiTest.setTestContent(scriptKey: Int? = null) {
		val viewModel = EditScriptViewModel(
			navController = navControllerMock,
			getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
			getScriptIdUseCase = getScriptIdUseCaseMock,
			executeScriptUseCase = executeScriptUseCaseMock,
			saveUserScriptUseCase = saveUserScriptUseCaseMock,
			removeUserScriptUseCase = removeUserScriptUseCaseMock,
			saveUserScriptUseCaseResultMapper = SaveUserScriptUseCaseResultMapper(),
			getDevicesUseCase = getDevicesUseCaseMock,
			mainDispatcher = Dispatchers.Unconfined,
			ioDispatcher = Dispatchers.Unconfined,
			scriptKey = scriptKey,
		)
		setContent {
			AppCommanderTheme(
				darkTheme = true,
				content = {
					EditScriptScreen(
						viewModel = viewModel,
					)
				},
			)
		}
	}
}
