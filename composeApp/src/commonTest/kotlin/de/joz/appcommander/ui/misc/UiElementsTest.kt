package de.joz.appcommander.ui.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class UiElementsTest {
	private val screenshotVerifier =
		ScreenshotVerifier(
			testClass = javaClass,
		)

	@Test
	fun `should render all ui elements correctly in dark mode`() {
		runComposeUiTest {
			setupTestUi(
				darkMode = true,
			)

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "all_ui_elements_in_dark_mode",
			)
		}
	}

	@Test
	fun `should render all ui elements correctly in light mode`() {
		runComposeUiTest {
			setupTestUi(
				darkMode = false,
			)

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "all_ui_elements_in_light_mode",
			)
		}
	}

	private fun ComposeUiTest.setupTestUi(darkMode: Boolean) {
		setContent {
			AppCommanderTheme(
				darkTheme = darkMode,
				content = {
					Column(
						modifier =
							Modifier
								.fillMaxSize()
								.background(MaterialTheme.colorScheme.background),
						verticalArrangement = Arrangement.SpaceEvenly,
					) {
						if (darkMode) {
							PreviewBottomBar_Dark()
							PreviewDevicesBar_Dark()
							PreviewExpandButton_Dark()
							PreviewLabelledSwitch_Dark()
							PreviewPlatformSelection_Dark()
							PreviewScriptInput_Dark()
							PreviewSectionDivider_Dark()
							PreviewSimpleTextInput_Dark()
							PreviewSlider_Dark()
							PreviewTextLabel_Dark()
							PreviewTitleBar_Dark()
						} else {
							PreviewBottomBar_Light()
							PreviewDevicesBar_Light()
							PreviewExpandButton_Light()
							PreviewLabelledSwitch_Light()
							PreviewPlatformSelection_Light()
							PreviewScriptInput_Light()
							PreviewSectionDivider_Light()
							PreviewSimpleTextInput_Light()
							PreviewSlider_Light()
							PreviewTextLabel_Light()
							PreviewTitleBar_Light()
						}
					}
				},
			)
		}
	}
}
