package de.joz.appcommander.ui.jsoneditor

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.joz.appcommander.data.ScriptsRepositoryImpl
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.action_abort
import de.joz.appcommander.resources.action_save
import de.joz.appcommander.resources.json_editor_open_script_file_externally
import de.joz.appcommander.resources.json_editor_title
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.internalpreviews.PreviewRenderContainer
import de.joz.appcommander.ui.misc.BottomBar
import de.joz.appcommander.ui.misc.BottomBarAction
import de.joz.appcommander.ui.misc.SectionDivider
import de.joz.appcommander.ui.misc.TextLabel
import de.joz.appcommander.ui.misc.TextLabelType
import de.joz.appcommander.ui.misc.TitleBar
import de.joz.appcommander.ui.theme.AppCommanderTheme
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource

// error handling, save and close button
@Composable
fun JsonEditorScreen(viewModel: JsonEditorViewModel) {
	val uiState = viewModel.uiState.collectAsStateWithLifecycle()

	JsonEditorContent(uiState = uiState.value, onEvent = {
		viewModel.onEvent(event = it)
	})
}

@Composable
internal fun JsonEditorContent(
	uiState: JsonEditorViewModel.UiState,
	onEvent: (JsonEditorViewModel.Event) -> Unit,
) {
	Scaffold(
		containerColor = MaterialTheme.colorScheme.surface,
		topBar = {
			TitleBar(
				title = stringResource(Res.string.json_editor_title),
				onBackNavigation = {
					onEvent(JsonEditorViewModel.Event.OnNavigateBack)
				},
			)
		},
		bottomBar = {
			BottomBar(
				actions = listOf(
					BottomBarAction(
						enabled = uiState.isJsonValid,
						label = Res.string.action_save,
						action = { onEvent(JsonEditorViewModel.Event.OnSaveScript) },
					),
					BottomBarAction(
						label = Res.string.json_editor_open_script_file_externally,
						action = { onEvent(JsonEditorViewModel.Event.OnOpenScriptFile) },
					),
					BottomBarAction(
						label = Res.string.action_abort,
						action = { onEvent(JsonEditorViewModel.Event.OnNavigateBack) },
					),
				),
			)
		},
	) { paddingValues ->
		Column(
			Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			if (uiState.isJsonValid.not()) {
				TextLabel(
					text = uiState.jsonValidMessage,
					textLabelType = TextLabelType.BodyMedium,
				)
				SectionDivider(verticalPadding = 8.dp)
			}

			TextField(
				value = uiState.json,
				onValueChange = {
					onEvent(JsonEditorViewModel.Event.OnJsonChange(json = it))
				},
				modifier = Modifier
					.fillMaxSize()
					.border(
						width = 1.dp,
						shape = RoundedCornerShape(size = 12f),
						color = if (uiState.isJsonValid) Color.Transparent else Color.Red,
					).verticalScroll(rememberScrollState())
					.testTag("json_editor"),
				textStyle = TextStyle(
					fontFamily = FontFamily.Monospace,
					color = MaterialTheme.colorScheme.onSurface,
					fontSize = MaterialTheme.typography.bodyLarge.fontSize,
				),
				visualTransformation = JsonVisualTransformation(),
				colors = TextFieldDefaults.colors(
					unfocusedContainerColor = MaterialTheme.colorScheme.surface,
					focusedContainerColor = MaterialTheme.colorScheme.surface,
					focusedIndicatorColor = Color.Transparent,
					unfocusedIndicatorColor = Color.Transparent,
				),
			)
		}
	}
}

@Preview
@Composable
internal fun PreviewPreviewJsonEditorContent() {
	PreviewRenderContainer { previewData ->
		PreviewJsonEditorContent(previewData)
	}
}

@Preview
@Composable
internal fun PreviewJsonEditorContent(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = previewData.uiState,
	) {
		JsonEditorContent(
			uiState = JsonEditorViewModel.UiState(
				json = Json.encodeToString(ScriptsRepositoryImpl.DEFAULT_SCRIPTS),
				jsonValidMessage = "",
			),
			onEvent = {},
		)
	}
}
