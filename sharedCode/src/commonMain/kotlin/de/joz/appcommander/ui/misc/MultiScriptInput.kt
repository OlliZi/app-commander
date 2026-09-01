package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Play
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.edit_enter_or_edit
import de.joz.appcommander.resources.edit_run_all_scripts
import de.joz.appcommander.ui.edit.EditScriptViewModel
import de.joz.appcommander.ui.internalpreviews.DarkLightPreviewContainerProvider
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun MultiScriptInput(
	executeScriptButtonEnabled: Boolean,
	scripts: List<EditScriptViewModel.SubScript>,
	onExecuteAllScriptsText: () -> Unit,
	onChangeScriptText: (Int, EditScriptViewModel.SubScript) -> Unit,
	onChangeScriptComment: (Int, String) -> Unit,
	onRemoveScript: (Int) -> Unit,
	onAddScriptText: (Int) -> Unit,
	onExecuteScriptText: (EditScriptViewModel.SubScript) -> Unit,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
	) {
		TextLabel(
			text = stringResource(Res.string.edit_enter_or_edit),
			textLabelType = TextLabelType.BodyLarge,
			modifier = Modifier.weight(1f),
		)

		TextLabel(
			text = stringResource(Res.string.edit_run_all_scripts),
			textLabelType = TextLabelType.BodyLarge,
		)
		IconButton(
			enabled = executeScriptButtonEnabled,
			onClick = onExecuteAllScriptsText,
		) {
			Icon(
				imageVector = FeatherIcons.Play,
				tint = if (executeScriptButtonEnabled) MaterialTheme.colorScheme.primary else LocalContentColor.current,
				contentDescription = "Execute all scripts",
			)
		}
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		scripts.forEachIndexed { index, script ->
			ScriptInput(
				executeScriptButtonEnabled = executeScriptButtonEnabled,
				showMoreButton = true,
				script = script,
				onExecuteScriptText = onExecuteScriptText,
				onChangeScriptText = { editedScript ->
					onChangeScriptText(index, editedScript)
				},
				onChangeScriptComment = { comment ->
					onChangeScriptComment(index, comment)
				},
				onAddScript = {
					onAddScriptText(index)
				},
				onRemoveScript = {
					onRemoveScript(index)
				},
			)
			if (index < scripts.lastIndex) {
				ScriptDivider()
			}
		}
	}
}

@Composable
private fun ScriptDivider() {
	HorizontalDivider(
		modifier = Modifier.padding(vertical = 4.dp),
	)
}

@Preview
@Composable
private fun PreviewMultiScriptInput() {
	DarkLightPreviewContainerProvider { darkMode ->
		PreviewMultiScriptInput(darkMode)
	}
}

@Composable
private fun PreviewMultiScriptInput(darkMode: Boolean) {
	AppCommanderTheme(
		darkTheme = darkMode,
	) {
		MultiScriptInput(
			executeScriptButtonEnabled = true,
			scripts = listOf("adb devices", "adb shell echo foo", "adb shell echo bar", "adb shell echo 123").map {
				EditScriptViewModel.SubScript(subScript = it)
			},
			onRemoveScript = {},
			onExecuteScriptText = {},
			onExecuteAllScriptsText = {},
			onAddScriptText = {},
			onChangeScriptText = { _, _ -> },
			onChangeScriptComment = { _, _ -> },
		)
	}
}
