package de.joz.appcommander.ui.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class UiElementsTest {
	private val screenshotVerifier = ScreenshotVerifier(
		testClass = javaClass,
	)

	@Test
	fun `should render PreviewBottomBar in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewBottomBar,
		)

	@Test
	fun `should render PreviewBottomBar in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewBottomBar,
		)

	@Test
	fun `should render PreviewPlatformIcon in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewPlatformIcon,
		)

	@Test
	fun `should render PreviewPlatformIcon in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewPlatformIcon,
		)

	@Test
	fun `should render PreviewConnectedDevices in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewConnectedDevices,
		)

	@Test
	fun `should render PreviewConnectedDevices in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewConnectedDevices,
		)

	@Test
	fun `should render PreviewMultiScriptInput in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewMultiScriptInput,
		)

	@Test
	fun `should render PreviewMultiScriptInput in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewMultiScriptInput,
		)

	@Test
	fun `should render PreviewCollapsable in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewCollapsable,
		)

	@Test
	fun `should render PreviewCollapsable in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewCollapsable,
		)

	@Test
	fun `should render PreviewConfirmation in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewConfirmation,
		)

	@Test
	fun `should render PreviewConfirmation in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewConfirmation,
		)

	@Test
	fun `should render PreviewExpandButton in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewExpandButton,
		)

	@Test
	fun `should render PreviewExpandButton in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewExpandButton,
		)

	@Test
	fun `should render PreviewLabelledSwitch in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewLabelledSwitch,
		)

	@Test
	fun `should render PreviewLabelledSwitch in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewLabelledSwitch,
		)

	@Test
	fun `should render PreviewPlatformSelection in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewPlatformSelection,
		)

	@Test
	fun `should render PreviewPlatformSelection in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewPlatformSelection,
		)

	@Test
	fun `should render PreviewScriptInput in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewScriptInput,
		)

	@Test
	fun `should render PreviewScriptInput in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewScriptInput,
		)

	@Test
	fun `should render PreviewSectionDivider in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewSectionDivider,
		)

	@Test
	fun `should render PreviewSectionDivider in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewSectionDivider,
		)

	@Test
	fun `should render PreviewSimpleTextInput in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewSimpleTextInput,
		)

	@Test
	fun `should render PreviewSimpleTextInput in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewSimpleTextInput,
		)

	@Test
	fun `should render PreviewSlider in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewSlider,
		)

	@Test
	fun `should render PreviewSlider in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewSlider,
		)

	@Test
	fun `should render PreviewTextLabel in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewTextLabel,
		)

	@Test
	fun `should render PreviewTextLabel in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewTextLabel,
		)

	@Test
	fun `should render PreviewTitleBar in all modes - light`() =
		setupTestUiElement(
			darkMode = false,
			content = ::PreviewTitleBar,
		)

	@Test
	fun `should render PreviewTitleBar in all modes - dark`() =
		setupTestUiElement(
			darkMode = true,
			content = ::PreviewTitleBar,
		)

	private fun ComposeUiTest.verifyScreenshot(screenshotName: String) {
		screenshotVerifier.verifyScreenshot(
			source = this,
			screenshotName = screenshotName,
		)
	}

	private fun setupTestUiElement(
		darkMode: Boolean,
		content: @Composable (Boolean) -> Unit,
	) {
		runComposeUiTest {
			setContent {
				AppCommanderTheme(darkTheme = darkMode, content = {
					Column(
						modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
					) {
						content(darkMode)
					}
				})
			}

			val screenshotName = content.toString().replace("fun ", "").replace("(kotlin.Boolean): kotlin.Unit", "")

			verifyScreenshot(
				screenshotName = screenshotName + "_" + if (darkMode) "dark" else "light",
			)
		}
	}
}
