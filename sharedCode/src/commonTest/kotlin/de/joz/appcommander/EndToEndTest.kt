package de.joz.appcommander

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import de.joz.appcommander.data.ScriptsRepositoryImpl
import de.joz.appcommander.domain.devices.GetDevicesUseCase
import de.joz.appcommander.domain.devices.ObserveDevicesUseCase
import de.joz.appcommander.domain.misc.ManageUiAppearanceUseCase
import de.joz.appcommander.domain.model.Device
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.preference.PreferencesRepository
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.helper.GetDevicesUseCaseMock
import de.joz.appcommander.helper.TestRuleApplier
import de.joz.appcommander.helper.screenshot.ScreenshotVerifier
import de.joz.appcommander.ui.settings.SettingsViewModel
import io.mockk.coEvery
import io.mockk.every
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

	private val testDevices = listOf(
		Device(id = "1", label = "emulator-5555", isSelected = false),
		Device(id = "2", label = "Google Pixel 10", isSelected = false),
	)

	@get:Rule
	val koinTestRule = KoinTestRule.create {
		modules(DependencyInjection().module)
		modules(
			module {
				single<SavePreferenceUseCase> { mockk(relaxed = true) }
				single<PreferencesRepository> { mockk(relaxed = true) }
				single<ScriptsRepository> {
					mockk {
						every { getScripts() } returns ScriptsRepository.JsonParseResult(
							scripts = ScriptsRepositoryImpl.DEFAULT_SCRIPTS,
							parsingMetaData = null,
						)
					}
				}
				single<GetScriptIdUseCase> {
					mockk {
						every { this@mockk(any()) } returns 0
					}
				}
				single<GetPreferenceUseCase> {
					mockk {
						coEvery {
							get(SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY, any<Boolean>())
						} returns false
						coEvery {
							get(ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE, any<Int>())
						} returns ManageUiAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex
						coEvery { get(any(), any<String>()) } returns ""
					}
				}
				single {
					mockk<ObserveDevicesUseCase>(relaxed = false) {
						every { this@mockk.invoke() } answers { flowOf(testDevices) }
					}
				}
				single<GetDevicesUseCase> {
					GetDevicesUseCaseMock { testDevices }
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
			onNodeWithText("App-Commander").assertIsDisplayed()
			onNodeWithText("Your bridge to connected devices").assertIsDisplayed()
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_1_welcome")

			onNodeWithText("Get started").performClick()

			// Step 2: Scripts Screen
			onNodeWithText("Your scripts").assertIsDisplayed()
			onNodeWithText("emulator-5555").assertIsDisplayed()
			onNodeWithText("Google Pixel 10").assertIsDisplayed()
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_2_scripts_main")

			// Select a device
			onNodeWithText("emulator-5555").performClick()

			// Expand and Execute a script
			onNodeWithTag("expand_button", useUnmergedTree = true).performClick()
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_3_scripts_expanded")
			onNodeWithTag("script_button_0").performClick()

			// Filtering
			onNodeWithTag("expand_button_filter").performClick()
			waitUntilAtLeastOneExists(hasTestTag("text_field_simple_text"))
			onNodeWithTag("text_field_simple_text").performTextInput("Dark")
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_4_scripts_filtered")

			// Terminal
			onNodeWithTag("expand_button_terminal").performClick()
			waitUntilAtLeastOneExists(hasTestTag("text_field_script_input"))
			onNodeWithTag("text_field_script_input").performTextInput("ls")
			onNodeWithContentDescription("Execute script text").assertIsEnabled()
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_5_scripts_terminal")

			// Step 3: Edit Script Screen
			onNodeWithContentDescription("Edit button").performClick()
			onNodeWithText("Edit script").assertIsDisplayed()

			onNodeWithTag("text_field_simple_text").performTextInput(" (Modified)")
			onNodeWithText("Desktop").performClick()
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_6_edit_script")

			// Confirmation dialog on abort
			onNodeWithText("Abort").performClick()
			onNodeWithText("Do you want to discard your changes?").assertIsDisplayed()
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_7_edit_abort_dialog")
			onNodeWithText("Dismiss").performClick()

			// Save and go back
			onNodeWithText("Save").performClick()

			// Step 4: Settings
			onNodeWithContentDescription("Settings").performClick()
			onNodeWithText("Settings").assertIsDisplayed()

			onNodeWithText("Show welcome screen").performScrollTo().assertIsDisplayed()
			onNodeWithTag("slider").performScrollTo().assertIsDisplayed()
			screenshotVerifier.verifyScreenshot(source = this, screenshotName = "e2e_8_settings")

			onNodeWithContentDescription("Back").performClick()
			onNodeWithText("Your scripts").assertIsDisplayed()
		}
	}
}
