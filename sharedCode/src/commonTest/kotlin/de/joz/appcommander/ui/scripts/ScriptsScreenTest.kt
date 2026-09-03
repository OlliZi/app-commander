package de.joz.appcommander.ui.scripts

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import de.joz.appcommander.DependencyInjection
import de.joz.appcommander.domain.devices.GetDevicesUseCase
import de.joz.appcommander.domain.devices.ObserveDevicesUseCase
import de.joz.appcommander.domain.model.Device
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.GetDevicesUseCaseMock
import de.joz.appcommander.helper.TestRuleApplier
import de.joz.appcommander.helper.screenshot.ScreenshotVerifier
import de.joz.appcommander.helper.toSubScripts
import de.joz.appcommander.ui.model.Hint
import de.joz.appcommander.ui.theme.AppCommanderTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.koin.dsl.module
import org.koin.ksp.generated.*
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ScriptsScreenTest :
	TestRuleApplier(),
	KoinTest {
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
	fun `should show default label when no devices are connected`() {
		runComposeUiTest {
			testDevices = emptyList()
			setTestContent(
				uiState = ScriptsViewModel.UiState(),
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
				uiState = ScriptsViewModel.UiState(
					scripts = listOf(
						ScriptsViewModel.Script(
							description = "Dark mode",
							comment = "some comment",
							scriptText = "adb shell cmd uimode night yes",
							originalScript = mockk {
								every { platform } returns ScriptsRepository.Platform.ANDROID
							},
						),
						ScriptsViewModel.Script(
							description = "Light mode",
							scriptText = "adb shell cmd uimode night no",
							originalScript = mockk {
								every { platform } returns ScriptsRepository.Platform.ANDROID
							},
						),
						ScriptsViewModel.Script(
							description = "Login into app",
							comment = "Automate login steps",
							scriptText = "adb shell input text \"USER\" && adb shell input \"HIDDEN\"",
							isExpanded = true,
							originalScript = mockk {
								every { platform } returns ScriptsRepository.Platform.ANDROID
							},
						),
						ScriptsViewModel.Script(
							description = "Swipe through app",
							comment = null,
							scriptText = "#LOOP_10 adb shell input swipe 500 500 500 500",
							originalScript = mockk {
								every { platform } returns ScriptsRepository.Platform.ANDROID
							},
						),
					),
					logging = listOf("1. adb devices", "2. adb shell cmd uimode night yes"),
				),
			)

			onNodeWithTag(
				testTag = "expand_button_logging",
			).assertIsDisplayed().performClick()

			onNodeWithTag(
				testTag = "expand_button_terminal",
			).assertIsDisplayed().performClick()

			onNodeWithTag(
				testTag = "expand_button_filter",
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
				uiState = ScriptsViewModel.UiState(
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
	fun `should all scripts when script is expanded`() {
		runComposeUiTest {
			setTestContent(
				uiState = ScriptsViewModel.UiState(
					scripts = listOf(
						ScriptsViewModel.Script(
							description = "some script",
							scriptText =
								"adb shell cmd uimode night no\n" + "sleep 1\n" + "adb shell cmd uimode night yes\n" + "sleep 1\n" +
									"adb shell cmd uimode night no",
							originalScript = ScriptsRepository.Script(
								label = "needed for platform",
								platform = ScriptsRepository.Platform.ANDROID,
								scripts = emptyList(),
							),
							isExpanded = true,
						),
					),
				),
			)

			onNodeWithTag(
				testTag = "expand_button_script_0",
			).assertIsDisplayed().performClick()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "expanded_script",
			)
		}
	}

	@Test
	fun `should always activate button for Desktop scripts regardless of connected devices`() {
		runComposeUiTest {
			setTestContent(
				uiState = ScriptsViewModel.UiState(
					scripts = ScriptsRepository.Platform.entries.map {
						ScriptsViewModel.Script(
							description = "some script for ${it.label}",
							scriptText = "echo Hello App-Commander!",
							originalScript = ScriptsRepository.Script(
								label = "",
								platform = it,
								scripts = emptyList(),
							),
							isExpanded = false,
						)
					},
				),
			)

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "activated_scripts",
			)
		}
	}

	@Test
	fun `should clear log when clear button is executed`() {
		runComposeUiTest {
			var isClearClicked = 0
			setTestContent(
				uiState = ScriptsViewModel.UiState(
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
				uiState = ScriptsViewModel.UiState(
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
				uiState = ScriptsViewModel.UiState(),
			)

			onNodeWithText("Hint: Activate the 'Developer options' on your device.").assertIsDisplayed()
			onNodeWithText("Your connected devices:").assertIsDisplayed()
			onNodeWithText("emulator-5555").assertIsDisplayed()
			onNodeWithText("emulator-5556").assertIsDisplayed()
			onNodeWithText("Google Pixel 10").assertIsDisplayed()
			onNodeWithText("Refresh").performClick()
		}
	}

	@Test
	fun `should refresh devices when refresh button is clicked`() {
		runComposeUiTest {
			setTestContent(
				uiState = ScriptsViewModel.UiState(),
				onEvent = {},
			)

			onNodeWithText("Refresh").performClick()

			assertEquals(1, getDevicesUseCaseMock.getCounterAndReset())
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

			onNodeWithText("adb shell input tap 200 200").assertIsDisplayed()
			onNodeWithContentDescription("Execute script text").assertIsDisplayed()

			ScriptsRepository.Platform.entries.forEach {
				onNodeWithText(it.label).assertIsDisplayed()
			}
		}
	}

	@Test
	fun `should not execute script when executed in terminal but there is no selected device for Android`() {
		`should not execute script when executed in terminal but there is no selected device for platform`(
			platform = ScriptsRepository.Platform.ANDROID,
			expectedIsEnabled = false,
		)
	}

	@Test
	fun `should not execute script when executed in terminal but there is no selected device for iOS`() {
		`should not execute script when executed in terminal but there is no selected device for platform`(
			platform = ScriptsRepository.Platform.IOS,
			expectedIsEnabled = false,
		)
	}

	@Test
	fun `should not execute script when executed in terminal but there is no selected device for Desktop`() {
		`should not execute script when executed in terminal but there is no selected device for platform`(
			platform = ScriptsRepository.Platform.DESKTOP,
			expectedIsEnabled = true,
		)
	}

	private fun `should not execute script when executed in terminal but there is no selected device for platform`(
		platform: ScriptsRepository.Platform,
		expectedIsEnabled: Boolean,
	) {
		runComposeUiTest {
			testDevices = emptyList()
			setTestContent(
				uiState = ScriptsViewModel.UiState(),
				onEvent = {},
			)

			onNodeWithTag(
				testTag = "expand_button_terminal",
			).assertIsDisplayed().performClick()

			waitUntilAtLeastOneExists(hasTestTag("text_field_script_input"))
			onNodeWithTag(testTag = "text_field_script_input").performTextClearance()
			onNodeWithTag(testTag = "text_field_script_input").performTextInput("foo bar")

			onNodeWithText(
				text = platform.label,
			).assertIsDisplayed().performClick()

			if (expectedIsEnabled) {
				onNodeWithContentDescription(label = "Execute script text").assertIsEnabled()
			} else {
				onNodeWithContentDescription(label = "Execute script text").assertIsNotEnabled()
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
				text = ScriptsRepository.Platform.DESKTOP.label,
			).assertIsDisplayed().performClick()

			onNodeWithContentDescription(label = "Execute script text").performClick()

			assertEquals("foo bar", selectedScriptText)
			assertEquals(ScriptsRepository.Platform.DESKTOP, selectedPlatform)
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
				uiState = ScriptsViewModel.UiState(
					hint = Hint.Error(Exception("Cannot find field 'platform'.")),
				),
			)

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "json_error_invalid_script",
			)
		}
	}

	@Test
	fun `should error if JSON contains multiple scripts`() {
		runComposeUiTest {
			setTestContent(
				uiState = ScriptsViewModel.UiState(
					hint = Hint.MultiScripts,
				),
			)

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "json_error_multiple_scripts",
			)
		}
	}

	@Test
	fun `should error if JSON contains an old script field`() {
		runComposeUiTest {
			setTestContent(
				uiState = ScriptsViewModel.UiState(
					hint = Hint.OldScriptFieldHint,
				),
			)

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "json_error_old_script_field",
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

			waitUntilAtLeastOneExists(hasTestTag("text_field_simple_filter"))
			onNodeWithTag(testTag = "text_field_simple_text_clear_text").performClick()
			onNodeWithTag(testTag = "text_field_simple_filter").performTextInput("filter")

			assertEquals(filterText, "filter")
		}
	}

	@Test
	fun `should run script on devices when multiples devices are selected`() =
		runComposeUiTest {
			val testScript = ScriptsViewModel.Script(
				scriptText = "echo bar",
				description = "my script",
				isExpanded = false,
				originalScript = ScriptsRepository.Script(
					label = "foo",
					scripts = listOf("echo bar").toSubScripts(),
					platform = ScriptsRepository.Platform.ANDROID,
				),
			)
			testDevices = listOf(
				Device(id = "1", label = "Pixel 1", isSelected = false),
				Device(id = "2", label = "Pixel 2", isSelected = false),
				Device(id = "3", label = "Pixel 3", isSelected = false),
			)

			val executeScriptEvents = mutableListOf<ScriptsViewModel.Event.OnExecuteScript>()
			setTestContent(
				uiState = ScriptsViewModel.UiState(
					scripts = listOf(testScript),
				),
				onEvent = {
					if (it is ScriptsViewModel.Event.OnExecuteScript) {
						executeScriptEvents.add(it)
					}
				},
			)

			onNodeWithText(text = "Pixel 1").assertHasClickAction()
			onNodeWithText(text = "Pixel 1").performClick()

			onNodeWithText(text = "Pixel 3").assertHasClickAction()
			onNodeWithText(text = "Pixel 3").performClick()

			waitUntilAtLeastOneExists(
				matcher = hasTestTag(testTag = "script_button_0") and isEnabled(),
				timeoutMillis = 3000L,
			)
			onNodeWithTag(testTag = "script_button_0").performClick()

			assertEquals(1, executeScriptEvents.size)
			assertEquals(testScript, executeScriptEvents.first().script)
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
