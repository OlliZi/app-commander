package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.FilePlus
import compose.icons.feathericons.Play
import compose.icons.feathericons.Trash
import de.joz.appcommander.ui.edit.EditScriptViewModel
import de.joz.appcommander.ui.internalpreviews.DarkLightPreviewContainerProvider
import de.joz.appcommander.ui.theme.AppCommanderTheme

@Composable
fun ScriptInput(
	isAtMinimumOneDeviceSelected: Boolean,
	onExecuteScriptText: (EditScriptViewModel.SubScript) -> Unit,
	script: EditScriptViewModel.SubScript = EditScriptViewModel.SubScript(subScript = ""),
	onChangeScriptText: (EditScriptViewModel.SubScript) -> Unit = { _ -> },
	onRemoveScript: (() -> Unit)? = null,
	onAddScript: (() -> Unit)? = null,
) {
	var inputValue by remember(script) { mutableStateOf(script.subScript) }
	TextField(
		value = inputValue,
		modifier = Modifier.fillMaxWidth().testTag("text_field_script_input"),
		colors = TextFieldDefaults.colors(
			unfocusedContainerColor = Color.White,
			focusedContainerColor = Color.White,
			focusedIndicatorColor = Color.Transparent,
			unfocusedIndicatorColor = Color.Transparent,
		),
		textStyle = TextStyle.Default.copy(
			color = Color.Black,
		),
		onValueChange = {
			inputValue = it
			onChangeScriptText(EditScriptViewModel.SubScript(subScript = it))
		},
		trailingIcon = {
			Row {
				ActionButtonIcon(
					icon = FeatherIcons.Trash,
					onAction = onRemoveScript,
					contentDescription = "Remove script",
				)
				ActionButtonIcon(
					icon = FeatherIcons.FilePlus,
					onAction = onAddScript,
					contentDescription = "Add script",
				)
				ActionButtonIcon(
					icon = FeatherIcons.Play,
					enabled = isAtMinimumOneDeviceSelected,
					contentDescription = "Execute script text",
					onAction = {
						onExecuteScriptText(EditScriptViewModel.SubScript(subScript = inputValue))
					},
				)
			}
		},
	)
}

@Composable
private fun ActionButtonIcon(
	icon: ImageVector,
	contentDescription: String,
	onAction: (() -> Unit)? = null,
	enabled: Boolean = true,
) {
	if (onAction == null) {
		return
	}

	IconButton(
		modifier = Modifier.size(36.dp),
		enabled = enabled,
		onClick = onAction,
	) {
		Icon(
			imageVector = icon,
			tint = if (enabled) MaterialTheme.colorScheme.primary else LocalContentColor.current,
			contentDescription = contentDescription,
		)
	}
}

@Preview
@Composable
internal fun PreviewScriptInput() {
	DarkLightPreviewContainerProvider { darkMode ->
		PreviewScriptInput(darkMode)
	}
}

@Composable
internal fun PreviewScriptInput(darkMode: Boolean) {
	AppCommanderTheme(
		darkTheme = darkMode,
	) {
		Column(
			verticalArrangement = Arrangement.SpaceBetween,
		) {
			ScriptInput(
				isAtMinimumOneDeviceSelected = true,
				script = EditScriptViewModel.SubScript(subScript = "adb devices"),
				onExecuteScriptText = {},
			)
		}
	}
}
