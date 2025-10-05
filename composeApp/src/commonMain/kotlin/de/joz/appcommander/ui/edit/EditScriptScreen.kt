package de.joz.appcommander.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.joz.appcommander.domain.ScriptsRepository
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.edit_enter_or_edit
import de.joz.appcommander.resources.edit_script_name
import de.joz.appcommander.resources.edit_select_platform
import de.joz.appcommander.resources.edit_title
import de.joz.appcommander.resources.enter_terminal_placeholder
import de.joz.appcommander.ui.misc.PlatformSelection
import de.joz.appcommander.ui.misc.ScriptInput
import de.joz.appcommander.ui.misc.SectionDivider
import de.joz.appcommander.ui.misc.SimpleTextInput
import de.joz.appcommander.ui.misc.TitleBar
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditScriptScreen(viewModel: EditScriptViewModel) {
	val uiState = viewModel.uiState.collectAsStateWithLifecycle()

	ScriptsContent(
		uiState = uiState.value,
		onBackNavigation = {
			viewModel.onEvent(event = EditScriptViewModel.Event.OnNavigateBack)
		},
		onSelectPlatform = {
			viewModel.onEvent(event = EditScriptViewModel.Event.OnSelectPlatform(platform = it))
		},
		onExecuteScriptText = {
			viewModel.onEvent(event = EditScriptViewModel.Event.OnExecuteScript)
		},
		onChangeScriptText = { script ->
			viewModel.onEvent(event = EditScriptViewModel.Event.OnChangeScript(script = script))
		},
		onChangeTextChange = { scriptName ->
			viewModel.onEvent(event = EditScriptViewModel.Event.OnChangeScriptName(scriptName = scriptName))
		},
	)
}

@Composable
internal fun ScriptsContent(
	uiState: EditScriptViewModel.UiState,
	onBackNavigation: () -> Unit,
	onSelectPlatform: (ScriptsRepository.Platform) -> Unit,
	onChangeScriptText: (String) -> Unit,
	onExecuteScriptText: () -> Unit,
	onChangeTextChange: (String) -> Unit,
) {
	Scaffold(
		containerColor = MaterialTheme.colorScheme.surface,
		topBar = {
			TitleBar(
				title = stringResource(Res.string.edit_title),
				onBackNavigation = onBackNavigation,
			)
		},
	) { paddingValues ->
		Column(
			Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			Text(
				text = stringResource(Res.string.edit_script_name),
				style = MaterialTheme.typography.bodyLarge,
			)
			SimpleTextInput(
				label = uiState.scriptName,
				onChangeTextChange = onChangeTextChange,
			)

			SectionDivider()

			Text(
				text = stringResource(Res.string.edit_enter_or_edit),
				style = MaterialTheme.typography.bodyLarge,
			)
			ScriptInput(
				placeHolder = stringResource(Res.string.enter_terminal_placeholder),
				onChangeScriptText = onChangeScriptText,
				onExecuteScriptText = {
					onExecuteScriptText()
				},
			)

			SectionDivider()

			Text(
				text = stringResource(Res.string.edit_select_platform),
				style = MaterialTheme.typography.bodyLarge,
			)
			PlatformSelection(
				selectedPlatform = uiState.selectedPlatform,
				onSelectPlatform = onSelectPlatform,
			)
		}
	}
}

@Preview
@Composable
private fun PreviewEditScriptScreen_Dark() {
	AppCommanderTheme(
		darkTheme = true,
	) {
		ScriptsContent(
			uiState = EditScriptViewModel.UiState(),
			onBackNavigation = {},
			onSelectPlatform = { _ -> },
			onChangeScriptText = { _ -> },
			onExecuteScriptText = {},
			onChangeTextChange = {},
		)
	}
}

@Preview
@Composable
private fun PreviewEditScriptScreen_Light() {
	AppCommanderTheme(
		darkTheme = false,
	) {
		ScriptsContent(
			uiState = EditScriptViewModel.UiState(),
			onBackNavigation = {},
			onSelectPlatform = { _ -> },
			onChangeScriptText = { _ -> },
			onExecuteScriptText = {},
			onChangeTextChange = {},
		)
	}
}
