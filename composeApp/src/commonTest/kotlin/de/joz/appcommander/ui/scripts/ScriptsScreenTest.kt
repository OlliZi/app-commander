package de.joz.appcommander.ui.scripts

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ScriptsScreenTest {
	private val screenshotVerifier =
		ScreenshotVerifier(
			testClass = javaClass,
		)

	@Test
	fun `should show default label when no devices are connected`() {
		runComposeUiTest {
			setTestContent(
				uiState =
					ScriptsViewModel.UiState(),
			)

			onNodeWithText("Your scripts").assertIsDisplayed()
			onNodeWithText("Hint: Activate the 'Developer options' on your device.").assertIsDisplayed()
			onNodeWithText("Connect your devices over USB and click refresh.").assertIsDisplayed()
			onNodeWithText("Refresh").assertIsDisplayed().assertHasClickAction()
			onNodeWithText("Filter").assertIsDisplayed()
			onNodeWithText("Terminal").assertIsDisplayed()
			onNodeWithText("Logging").assertIsDisplayed()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "default_label",
			)
		}
	}

	@Test
	fun `should show scripts label when scripts are available`() {
		runComposeUiTest {
			setTestContent(
				uiState =
					ScriptsViewModel.UiState(
						connectedDevices =
							listOf(
								ScriptsViewModel.Device(
									label = "emulator-5555",
									id = "1",
									isSelected = true,
								),
								ScriptsViewModel.Device(
									label = "emulator-5556",
									id = "2",
									isSelected = false,
								),
								ScriptsViewModel.Device(
									label = "Google Pixel 10",
									id = "3",
									isSelected = true,
								),
							),
						scripts =
							listOf(
								ScriptsViewModel.Script(
									description = "Dark mode",
									scriptText = "adb shell cmd uimode night yes",
									originalScript = mockk(),
								),
								ScriptsViewModel.Script(
									description = "Light mode",
									scriptText = "adb shell cmd uimode night no",
									originalScript = mockk(),
								),
								ScriptsViewModel.Script(
									description = "Login into app",
									scriptText = "adb shell input text \"USER\" && adb shell input \"HIDDEN\"",
									originalScript = mockk(),
								),
								ScriptsViewModel.Script(
									description = "Swipe through app",
									scriptText = "#LOOP_10 adb shell input swipe 500 500 500 500",
									originalScript = mockk(),
								),
							),
						logging = listOf("1. adb devices", "2. adb shell cmd uimode night yes"),
					),
			)

			onNodeWithTag(
				testTag = "expand_button_logging",
			).assertIsDisplayed().performClick()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "show_all",
			)
		}
	}

	@Test
	fun `should show log if expand button is clicked`() {
		runComposeUiTest {
			setTestContent(
				uiState =
					ScriptsViewModel.UiState(
						logging = listOf("Log abc", "Log 123"),
					),
			)

			onNodeWithTag(
				testTag = "expand_button_logging",
			).assertIsDisplayed().performClick()

			onNodeWithText("Log abc").assertIsDisplayed()
			onNodeWithText("Log 123").assertIsDisplayed()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "see_log",
			)
		}
	}

	@Test
	fun `should clear log when clear button is executed`() {
		runComposeUiTest {
			var isClearClicked = 0
			setTestContent(
				uiState =
					ScriptsViewModel.UiState(
						logging = listOf("Log abc", "Log 123"),
					),
				onEvent = {
					isClearClicked++
				},
			)

			onNodeWithContentDescription(
				label = "clear logging",
			).assertDoesNotExist()

			onNodeWithTag(testTag = "expand_button_logging").performClick()

			onNodeWithContentDescription(
				label = "clear logging",
			).assertIsDisplayed().performClick()

			assertEquals(1, isClearClicked)
		}
	}

	@Test
	fun `should collapse log when collapse button is executed`() {
		runComposeUiTest {
			setTestContent(
				uiState =
					ScriptsViewModel.UiState(
						logging = listOf("Log abc", "Log 123"),
					),
			)

			onNodeWithTag(
				testTag = "expand_button_logging",
			).assertIsDisplayed().performClick()

			onNodeWithText("Log abc").assertIsDisplayed().assertExists()
			onNodeWithText("Log 123").assertIsDisplayed().assertExists()

			onNodeWithTag(
				testTag = "expand_button_logging",
			).assertIsDisplayed().performClick()

			onNodeWithText("Log abc").assertDoesNotExist()
			onNodeWithText("Log 123").assertDoesNotExist()
		}
	}

	@Test
	fun `should show connected devices`() {
		runComposeUiTest {
			setTestContent(
				uiState =
					ScriptsViewModel.UiState(
						connectedDevices =
							listOf(
								ScriptsViewModel.Device(
									label = "Device A",
									id = "1",
									isSelected = true,
								),
							),
					),
			)

			onNodeWithText("Hint: Activate the 'Developer options' on your device.").assertIsDisplayed()
			onNodeWithText("Your connected devices:").assertIsDisplayed()
			onNodeWithText("Device A").assertIsDisplayed()
			onNodeWithText("Refresh").performClick()
		}
	}

	@Test
	fun `should refresh devices when refresh button is clicked`() {
		runComposeUiTest {
			var isRefreshClicked = 0
			setTestContent(
				uiState = ScriptsViewModel.UiState(),
				onEvent = {
					isRefreshClicked++
				},
			)

			onNodeWithText("Refresh").performClick()

			assertEquals(1, isRefreshClicked)
		}
	}

	@Test
	fun `should open script file when open button is clicked`() {
		runComposeUiTest {
			var isOpenClicked = 0
			setTestContent(
				uiState = ScriptsViewModel.UiState(),
				onEvent = {
					isOpenClicked++
				},
			)

			onNodeWithText("Open script file").performClick()

			assertEquals(1, isOpenClicked)
		}
	}

	@Test
	fun `should show terminal screen when open button is clicked`() {
		runComposeUiTest {
			setTestContent(
				uiState = ScriptsViewModel.UiState(),
				onEvent = {},
			)

			onNodeWithTag(
				testTag = "expand_button_terminal",
			).assertIsDisplayed().performClick()

			onNodeWithText("adb devices").assertIsDisplayed()
			onNodeWithContentDescription("Execute script text").assertIsDisplayed()

			ScriptsRepository.Platform.entries.forEach {
				onNodeWithText(it.label).assertIsDisplayed()
			}
		}
	}

	@Test
	fun `should execute script when executed in terminal`() {
		runComposeUiTest {
			var selectedScriptText = ""
			var selectedPlatform: ScriptsRepository.Platform? = null
			setTestContent(
				uiState = ScriptsViewModel.UiState(),
				onEvent = {
					if (it is ScriptsViewModel.Event.OnExecuteScriptText) {
						selectedScriptText = it.script
						selectedPlatform = it.platform
					}
				},
			)

			onNodeWithTag(
				testTag = "expand_button_terminal",
			).assertIsDisplayed().performClick()

			waitUntilAtLeastOneExists(hasTestTag("text_field_script_input"))
			onNodeWithTag(testTag = "text_field_script_input").performTextClearance()
			onNodeWithTag(testTag = "text_field_script_input").performTextInput("foo bar")

			onNodeWithText(
				text = ScriptsRepository.Platform.IOS.label,
			).assertIsDisplayed().performClick()

			onNodeWithContentDescription(label = "Execute script text").performClick()

			assertEquals("foo bar", selectedScriptText)
			assertEquals(ScriptsRepository.Platform.IOS, selectedPlatform)
		}
	}

	@Test
	fun `should open new script screen when button is clicked`() {
		runComposeUiTest {
			var onNewScriptFileCounter = 0
			setTestContent(
				uiState = ScriptsViewModel.UiState(),
				onEvent = {
					onNewScriptFileCounter++
				},
			)

			onNodeWithText(
				text = "Add new script",
			).assertIsDisplayed().performClick()

			assertEquals(1, onNewScriptFileCounter)
		}
	}

	@Test
	fun `should error if JSON contains invalid scripts`() {
		runComposeUiTest {
			setTestContent(
				uiState =
					ScriptsViewModel.UiState(
						jsonParsingError = "Cannot find field 'platform'.",
					),
			)

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "json_error",
			)
		}
	}

	@Test
	fun `should show filter screen when open button is clicked`() {
		runComposeUiTest {
			setTestContent(
				uiState = ScriptsViewModel.UiState(),
				onEvent = {},
			)

			onNodeWithTag(
				testTag = "expand_button_filter",
			).assertIsDisplayed().performClick()
		}
	}

	@Test
	fun `should filter scripts when filter is typed`() {
		runComposeUiTest {
			var filterText = ""

			setTestContent(
				uiState = ScriptsViewModel.UiState(),
				onEvent = {
					if (it is ScriptsViewModel.Event.OnFilterScripts) {
						filterText = it.filter
					}
				},
			)

			onNodeWithTag(
				testTag = "expand_button_filter",
			).assertIsDisplayed().performClick()

			waitUntilAtLeastOneExists(hasTestTag("text_field_simple_text"))
			onNodeWithTag(testTag = "text_field_simple_text").performTextClearance()
			onNodeWithTag(testTag = "text_field_simple_text").performTextInput("filter")

			assertEquals(filterText, "filter")
		}
	}

	private fun ComposeUiTest.setTestContent(
		uiState: ScriptsViewModel.UiState,
		onEvent: (ScriptsViewModel.Event) -> Unit = {},
	) {
		setContent {
			AppCommanderTheme(
				darkTheme = true,
				content = {
					ScriptsContent(
						uiState = uiState,
						onEvent = onEvent,
					)
				},
			)
		}
	}
}
