package de.joz.appcommander.ui.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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

			verifyScreenshot(
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

			verifyScreenshot(
				screenshotName = "all_ui_elements_in_light_mode",
			)
		}
	}

	@Test
	fun `should render PreviewBottomBar in all modes`() =
		setupTestUiElement("PreviewBottomBar") {
			PreviewBottomBar()
		}

	@Test
	fun `should render PreviewDevicesBar in all modes`() =
		setupTestUiElement("PreviewDevicesBar") {
			PreviewDevicesBar()
		}

	@Test
	fun `should render PreviewExpandButton in all modes`() =
		setupTestUiElement("PreviewExpandButton") {
			PreviewExpandButton()
		}

	@Test
	fun `should render PreviewLabelledSwitch in all modes`() =
		setupTestUiElement("PreviewLabelledSwitch") {
			PreviewLabelledSwitch()
		}

	@Test
	fun `should render PreviewPlatformSelection in all modes`() =
		setupTestUiElement("PreviewPlatformSelection") {
			PreviewPlatformSelection()
		}

	@Test
	fun `should render PreviewScriptInput in all modes`() =
		setupTestUiElement("PreviewScriptInput") {
			PreviewScriptInput()
		}

	@Test
	fun `should render PreviewSectionDivider in all modes`() =
		setupTestUiElement("PreviewSectionDivider") {
			PreviewSectionDivider()
		}

	@Test
	fun `should render PreviewSimpleTextInput in all modes`() =
		setupTestUiElement("PreviewSimpleTextInput") {
			PreviewSimpleTextInput()
		}

	@Test
	fun `should render PreviewSlider in all modes`() =
		setupTestUiElement("PreviewSlider") {
			PreviewSlider()
		}

	@Test
	fun `should render PreviewTextLabel in all modes`() =
		setupTestUiElement("PreviewTextLabel") {
			PreviewTextLabel()
		}

	@Test
	fun `should render PreviewTitleBar in all modes`() =
		setupTestUiElement("PreviewTitleBar") {
			PreviewTitleBar()
		}

	private fun ComposeUiTest.verifyScreenshot(screenshotName: String) {
		screenshotVerifier.verifyScreenshot(
			source = this,
			screenshotName = screenshotName,
		)
	}

	private fun setupTestUiElement(
		screenshotName: String,
		content: @Composable () -> Unit,
	) {
		runComposeUiTest {
			setContent {
				Column(
					modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
				) {
					content()
				}
			}

			verifyScreenshot(
				screenshotName = screenshotName,
			)
		}
	}

	private fun ComposeUiTest.setupTestUi(darkMode: Boolean) {
		setContent {
			AppCommanderTheme(
				darkTheme = darkMode,
				content = {
					Column(
						modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
					) {
						val uiState = PreviewData.createThemeDarkMode(darkMode)
						PreviewBottomBar(uiState)
						PreviewDevicesBar(uiState)
						PreviewExpandButton(uiState)
						PreviewLabelledSwitch(uiState)
						PreviewPlatformSelection(uiState)
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
