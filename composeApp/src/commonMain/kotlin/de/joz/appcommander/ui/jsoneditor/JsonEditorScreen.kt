package de.joz.appcommander.ui.jsoneditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.joz.appcommander.data.ScriptsRepositoryImpl
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.json_editor_open_script_file_external
import de.joz.appcommander.resources.json_editor_title
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.internalpreviews.PreviewRenderContainer
import de.joz.appcommander.ui.misc.BottomBar
import de.joz.appcommander.ui.misc.BottomBarAction
import de.joz.appcommander.ui.misc.TitleBar
import de.joz.appcommander.ui.theme.AppCommanderTheme
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource

@Composable
fun JsonEditorScreen(viewModel: JsonEditorViewModel) {
	val uiState = viewModel.uiState.collectAsStateWithLifecycle()

	JsonEditorContent(json = uiState.value.json, onEvent = {
		viewModel.onEvent(event = it)
	})
}

@Composable
private fun JsonEditorContent(
	json: String,
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
						label = Res.string.json_editor_open_script_file_external,
						action = {
							onEvent(JsonEditorViewModel.Event.OnOpenScriptFile)
						},
					),
				),
			)
		},
	) { paddingValues ->
		Column(
			Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(16.dp)
				.verticalScroll(rememberScrollState()),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			TextField(
				value = json,
				onValueChange = {
					onEvent(JsonEditorViewModel.Event.OnJsonChange(json = it))
				},
				modifier = Modifier.fillMaxSize(),
				textStyle = TextStyle(
					fontFamily = FontFamily.Monospace,
					color = MaterialTheme.colorScheme.onSurface,
				),
				visualTransformation = JsonVisualTransformation(),
				colors = TextFieldDefaults.colors(
					unfocusedContainerColor = MaterialTheme.colorScheme.surface,
					focusedContainerColor = MaterialTheme.colorScheme.surface,
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
			json = Json.encodeToString(ScriptsRepositoryImpl.DEFAULT_SCRIPTS),
			onEvent = {},
		)
	}
}
