package de.joz.appcommander.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.edit_action_abort
import de.joz.appcommander.resources.edit_action_remove
import de.joz.appcommander.resources.edit_action_save
import de.joz.appcommander.resources.edit_enter_or_edit
import de.joz.appcommander.resources.edit_script_name
import de.joz.appcommander.resources.edit_script_placeholder
import de.joz.appcommander.resources.edit_select_devices
import de.joz.appcommander.resources.edit_select_platform
import de.joz.appcommander.resources.edit_title
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.misc.BottomBar
import de.joz.appcommander.ui.misc.BottomBarAction
import de.joz.appcommander.ui.misc.DevicesBar
import de.joz.appcommander.ui.misc.PlatformSelection
import de.joz.appcommander.ui.misc.ScriptInput
import de.joz.appcommander.ui.misc.SectionDivider
import de.joz.appcommander.ui.misc.SimpleTextInput
import de.joz.appcommander.ui.misc.TextLabel
import de.joz.appcommander.ui.misc.TextLabelType
import de.joz.appcommander.ui.misc.TitleBar
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditScriptScreen(viewModel: EditScriptViewModel) {
	val uiState = viewModel.uiState.collectAsStateWithLifecycle()

	EditScriptContent(
		uiState = uiState.value,
		onEvent = viewModel::onEvent,
	)
}

@Composable
internal fun EditScriptContent(
	uiState: EditScriptViewModel.UiState,
	onEvent: (EditScriptViewModel.Event) -> Unit,
) {
	Scaffold(
		containerColor = MaterialTheme.colorScheme.surface,
		topBar = {
			TitleBar(
				title = stringResource(Res.string.edit_title),
				onBackNavigation = { onEvent(EditScriptViewModel.Event.OnNavigateBack) },
			)
		},
		bottomBar = {
			BottomBar(
				actions =
					listOf(
						BottomBarAction(
							label = Res.string.edit_action_save,
							action = {
								onEvent(EditScriptViewModel.Event.OnSaveScript)
							},
						),
						BottomBarAction(
							label = Res.string.edit_action_remove,
							action = {
								onEvent(EditScriptViewModel.Event.OnRemoveScript)
							},
						),
						BottomBarAction(
							label = Res.string.edit_action_abort,
							action = { onEvent(EditScriptViewModel.Event.OnNavigateBack) },
						),
					),
			)
		},
	) { paddingValues ->
		Column(
			Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			TextLabel(
				text = stringResource(Res.string.edit_script_name),
				textLabelType = TextLabelType.BodyLarge,
			)
			SimpleTextInput(
				value = uiState.scriptName,
				onChangeTextChange = {
					onEvent(EditScriptViewModel.Event.OnChangeScriptName(scriptName = it))
				},
			)

			SectionDivider()

			TextLabel(
				text = stringResource(Res.string.edit_enter_or_edit),
				textLabelType = TextLabelType.BodyLarge,
			)
			ScriptInput(
				script =
					uiState.script.ifEmpty {
						stringResource(
							Res.string.edit_script_placeholder,
						)
					},
				onChangeScriptText = {
					onEvent(EditScriptViewModel.Event.OnChangeScript(script = it))
				},
				onExecuteScriptText = {
					onEvent(EditScriptViewModel.Event.OnExecuteScript)
				},
			)

			SectionDivider()

			TextLabel(
				text = stringResource(Res.string.edit_select_platform),
				textLabelType = TextLabelType.BodyLarge,
			)
			PlatformSelection(
				selectedPlatform = uiState.selectedPlatform,
				onSelectPlatform = {
					onEvent(EditScriptViewModel.Event.OnSelectPlatform(platform = it))
				},
			)

			SectionDivider()

			TextLabel(
				text = stringResource(Res.string.edit_select_devices),
				textLabelType = TextLabelType.BodyLarge,
			)
			DevicesBar(
				connectedDevices = emptyList(),
				onDeviceSelect = {
				},
				onRefreshDevices = {
				},
			)
		}
	}
}

@Preview
@Composable
private fun PreviewEditScriptScreen(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = previewData.uiState,
	) {
		EditScriptContent(
			uiState = EditScriptViewModel.UiState(),
			onEvent = {},
		)
	}
}
