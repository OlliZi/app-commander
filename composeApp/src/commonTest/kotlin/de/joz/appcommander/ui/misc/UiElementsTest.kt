package de.joz.appcommander.ui.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.ui.internalpreviews.PreviewData
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
					) {
						val uiState = PreviewData.createThemeDarkMode(darkMode)
						PreviewBottomBar(uiState)
						PreviewDevicesBar(uiState)
						PreviewExpandButton(uiState)
						PreviewLabelledSwitch(uiState)
						PlatformSelectionPreviewParameterProvider().values.forEach {
							PreviewPlatformSelection(it)
						}
						PreviewScriptInput(uiState)
						PreviewSectionDivider(uiState)
						PreviewSimpleTextInput(uiState)
						PreviewSlider(uiState)
						PreviewTextLabel(uiState)
						PreviewTitleBar(uiState)
					}
				},
			)
		}
	}
}
