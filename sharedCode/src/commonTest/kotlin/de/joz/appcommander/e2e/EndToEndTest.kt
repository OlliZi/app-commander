package de.joz.appcommander.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import compose.icons.FeatherIcons
import compose.icons.feathericons.Settings
import de.joz.appcommander.App
import de.joz.appcommander.DependencyInjection
import de.joz.appcommander.domain.devices.GetConnectedDevicesUseCase
import de.joz.appcommander.domain.devices.ObserveDevicesUseCase
import de.joz.appcommander.domain.logging.GetLoggingUseCase
import de.joz.appcommander.domain.model.Device
import de.joz.appcommander.domain.preference.PreferencesRepository
import de.joz.appcommander.domain.script.RunFileBackupUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.domain.script.TrackScriptsFileChangesUseCase
import de.joz.appcommander.helper.PreferencesRepositoryMock
import de.joz.appcommander.helper.ScriptsRepositoryFake
import de.joz.appcommander.helper.TestRuleApplier
import de.joz.appcommander.helper.assertIsDisplayed
import de.joz.appcommander.helper.click
import de.joz.appcommander.helper.screenshot.ScreenshotVerifier
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.confirmation_no
import de.joz.appcommander.resources.edit_action_abort
import de.joz.appcommander.resources.edit_action_save
import de.joz.appcommander.resources.edit_confirmation_change
import de.joz.appcommander.resources.edit_title
import de.joz.appcommander.resources.scripts_title
import de.joz.appcommander.resources.settings_preference_show_welcome_screen
import de.joz.appcommander.resources.settings_title
import de.joz.appcommander.resources.welcome_action
import de.joz.appcommander.resources.welcome_catch_phrase
import de.joz.appcommander.resources.welcome_title
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.koin.dsl.module
import org.koin.ksp.generated.*
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class EndToEndTest :
	TestRuleApplier(),
	KoinTest {
	private val screenshotVerifier = ScreenshotVerifier(
		testClass = javaClass,
	)

	private val testScripts = listOf(
		ScriptsRepository.Script(
			label = "Dark mode",
			scripts = listOf("adb shell cmd uimode night yes"),
			platform = ScriptsRepository.Platform.ANDROID,
		),
		ScriptsRepository.Script(
			label = "Toggle dark to light mode",
			scripts = listOf(
				"adb shell cmd uimode night yes",
				"adb shell cmd uimode night no",
			),
			platform = ScriptsRepository.Platform.ANDROID,
		),
		ScriptsRepository.Script(
			label = "Light mode",
			scripts = listOf("adb shell cmd uimode night no"),
			platform = ScriptsRepository.Platform.ANDROID,
		),
		ScriptsRepository.Script(
			label = "Hello Test",
			scripts = listOf("echo hello test"),
			platform = ScriptsRepository.Platform.DESKTOP,
		),
	)
	private val testDevices = listOf(
		GetConnectedDevicesUseCase.ConnectedDevice(id = "1", label = "emulator-5555"),
		GetConnectedDevicesUseCase.ConnectedDevice(id = "2", label = "Google Pixel 10"),
	)

	@get:Rule
	val koinTestRule = KoinTestRule.create {
		modules(DependencyInjection().module)
		modules(
			module {
				single<PreferencesRepository> { PreferencesRepositoryMock() }
				single<ScriptsRepository> { ScriptsRepositoryFake(scripts = testScripts) }
				single<GetConnectedDevicesUseCase> {
					mockk {
						coEvery { this@mockk.invoke() } returns testDevices
					}
				}
				single<ObserveDevicesUseCase> {
					mockk {
						coEvery { this@mockk.invoke() } returns flowOf(
							testDevices.map {
								Device(
									it.id,
									it.label,
									false,
								)
							},
						)
					}
				}

				single<TrackScriptsFileChangesUseCase> { mockk(relaxed = true) }
				single<GetLoggingUseCase> {
					mockk {
						coEvery { this@mockk.invoke() } returns flowOf(emptyList())
					}
				}
			},
		)
	}

	@Test
	fun `should navigate through entire app and perform deep interactions`() {
		runComposeUiTest {
			setContent {
				App()
			}

			// Step 1: Welcome Screen
			assertIsDisplayed(Res.string.welcome_title)
			assertIsDisplayed(Res.string.welcome_catch_phrase)
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_1_welcome")

			click(Res.string.welcome_action)

			// Step 2: Scripts Screen
			assertIsDisplayed("Your scripts")
			assertIsDisplayed("emulator-5555")
			assertIsDisplayed("Google Pixel 10")
			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "e2e_2_scripts_main",
			)

			// Select a device
			click("emulator-5555")

			// Expand and Execute a script
			click("expand_button_script_1")
			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "e2e_3_scripts_expanded",
			)
			click("script_button_1")

			// Filtering
			click("expand_button_filter")
			waitUntilAtLeastOneExists(hasTestTag("text_field_simple_text"))
			onNodeWithTag("text_field_simple_text").performTextInput("Toggle dark")
			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "e2e_4_scripts_filtered",
			)

			// Terminal
			click("expand_button_terminal")
			waitUntilAtLeastOneExists(hasTestTag("text_field_script_input"))
			onNodeWithTag("text_field_script_input").performTextInput("ls")
			onNodeWithContentDescription("Execute script text").assertIsEnabled()
			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "e2e_5_scripts_terminal",
			)
			click("expand_button_terminal")

			// Logging?

			// Step 3: Edit Script Screen
			onNodeWithContentDescription("Edit button").performClick()
			assertIsDisplayed(Res.string.edit_title)

			onNodeWithTag("text_field_simple_text").performTextInput(" (Modified)")
			click("Desktop")
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_6_edit_script")

			// Confirmation dialog on abort
			click(Res.string.edit_action_abort)
			assertIsDisplayed(Res.string.edit_confirmation_change)
			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "e2e_7_edit_abort_dialog",
			)
			click(Res.string.confirmation_no)

			// Save and go back
			click(Res.string.edit_action_save)

			// Step 4: Settings
			click("action_button_${FeatherIcons.Settings.name}")
			assertIsDisplayed(Res.string.settings_title)

			assertIsDisplayed(Res.string.settings_preference_show_welcome_screen)
			onNodeWithTag(RunFileBackupUseCase.STORE_KEY_FOR_BACKUP_STORAGE).performScrollTo().assertIsDisplayed()
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_8_settings")

			click("back_button")
			assertIsDisplayed(Res.string.scripts_title)

			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_9_end")
		}
	}
}
