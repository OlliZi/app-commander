package de.joz.appcommander.ui.jsoneditor

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import de.joz.appcommander.data.ScriptsRepositoryImpl
import de.joz.appcommander.helper.ScreenshotVerifier
import de.joz.appcommander.ui.theme.AppCommanderTheme
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class JsonEditorScreenTest {
	private val jsonParser = Json { prettyPrint = true }
	private val screenshotVerifier = ScreenshotVerifier(
		testClass = javaClass,
	)

	@Test
	fun `should show default label and JSON when scripts are loaded`() {
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

	@Test
	fun `should save scripts when save button is clicked`() {
		runComposeUiTest {
			val testScripts = ScriptsRepositoryImpl.DEFAULT_SCRIPTS
			var isButtonClicked = 0

			setTestContent(
				uiState = JsonEditorViewModel.UiState(
					json = jsonParser.encodeToString(testScripts),
				),
				onEvent = {
					assertTrue(it is JsonEditorViewModel.Event.OnSaveScript)
					isButtonClicked += 1
				},
			)

			onNodeWithText("Save").performClick()

			assertEquals(1, isButtonClicked)
		}
	}

	@Test
	fun `should close screen when close button is clicked`() {
		runComposeUiTest {
			val testScripts = ScriptsRepositoryImpl.DEFAULT_SCRIPTS
			var isButtonClicked = 0

			setTestContent(
				uiState = JsonEditorViewModel.UiState(
					json = jsonParser.encodeToString(testScripts),
				),
				onEvent = {
					assertTrue(it is JsonEditorViewModel.Event.OnNavigateBack)
					isButtonClicked += 1
				},
			)

			onNodeWithText("Close").performClick()

			assertEquals(1, isButtonClicked)
		}
	}

	@Test
	fun `should open script externally when externally button is clicked`() {
		runComposeUiTest {
			val testScripts = ScriptsRepositoryImpl.DEFAULT_SCRIPTS
			var isButtonClicked = 0

			setTestContent(
				uiState = JsonEditorViewModel.UiState(
					json = jsonParser.encodeToString(testScripts),
				),
				onEvent = {
					assertTrue(it is JsonEditorViewModel.Event.OnOpenScriptFile)
					isButtonClicked += 1
				},
			)

			onNodeWithText("Open script file externally").performClick()

			assertEquals(1, isButtonClicked)
		}
	}

	@Test
	fun `should edit script when JSON is modified`() {
		runComposeUiTest {
			val testScripts = ScriptsRepositoryImpl.DEFAULT_SCRIPTS
			val jsonString = jsonParser.encodeToString(testScripts)
			var isEventFired = 0

			setTestContent(
				uiState = JsonEditorViewModel.UiState(
					json = jsonString,
				),
				onEvent = {
					assertIs<JsonEditorViewModel.Event.OnJsonChange>(it)
					assertEquals("hello$jsonString", it.json)
					isEventFired += 1
				},
			)

			onNodeWithTag(testTag = "json_editor").performTextInput("hello")

			assertEquals(1, isEventFired)
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
