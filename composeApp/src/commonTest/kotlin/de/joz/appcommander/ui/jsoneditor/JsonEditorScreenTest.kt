package de.joz.appcommander.ui.jsoneditor

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import de.joz.appcommander.data.ScriptsRepositoryImpl
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import kotlinx.serialization.json.Json
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class JsonEditorScreenTest {
	private val jsonParser = Json { prettyPrint = true }
	private val screenshotVerifier = ScreenshotVerifier(
		testClass = javaClass,
	)

	@Test
	fun `should show default label when no devices are connected`() {
		runComposeUiTest {
			val testScripts = ScriptsRepositoryImpl.DEFAULT_SCRIPTS
			setTestContent(
				uiState = JsonEditorViewModel.UiState(
					json = jsonParser.encodeToString(testScripts),
				),
			)

			onNodeWithText("Open script file externally").assertHasClickAction()
			onNodeWithText("Save").assertHasClickAction()
			onNodeWithText("Close").assertHasClickAction()

			screenshotVerifier.verifyScreenshot(
				source = this,
				screenshotName = "json_editor",
			)
		}
	}

	private fun ComposeUiTest.setTestContent(
		uiState: JsonEditorViewModel.UiState,
		onEvent: (JsonEditorViewModel.Event) -> Unit = {},
	) {
		setContent {
			AppCommanderTheme(
				darkTheme = true,
				content = {
					JsonEditorContent(
						uiState = uiState,
						onEvent = onEvent,
					)
				},
			)
		}
	}
}
