package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import compose.icons.FeatherIcons
import compose.icons.feathericons.FilePlus
import compose.icons.feathericons.Play
import compose.icons.feathericons.Trash
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.internalpreviews.PreviewRenderContainer
import de.joz.appcommander.ui.theme.AppCommanderTheme

@Composable
fun ScriptInput(
	onExecuteScriptText: (String) -> Unit,
	script: String = "",
	onChangeScriptText: (String) -> Unit = { _ -> },
	onRemoveScript: (() -> Unit)? = null,
	onAddScript: (() -> Unit)? = null,
) {
	var inputValue by remember(script) { mutableStateOf(script) }
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
			onChangeScriptText(it)
		},
		trailingIcon = {
			Row {
				RemoveIcon(
					onRemoveScript = onRemoveScript,
				)
				AddIcon(
					onAddScript = onAddScript,
				)
				PlayIcon(
					onExecuteScriptText = {
						onExecuteScriptText(inputValue)
					},
				)
			}
		},
	)
}

@Composable
private fun RemoveIcon(onRemoveScript: (() -> Unit)?) {
	if (onRemoveScript == null) {
		return
	}

	IconButton(
		onClick = onRemoveScript,
	) {
		Icon(
			imageVector = FeatherIcons.Trash,
			tint = MaterialTheme.colorScheme.primary,
			contentDescription = "Remove script",
		)
	}
}

@Composable
private fun AddIcon(onAddScript: (() -> Unit)?) {
	if (onAddScript == null) {
		return
	}

	IconButton(
		onClick = onAddScript,
	) {
		Icon(
			imageVector = FeatherIcons.FilePlus,
			tint = MaterialTheme.colorScheme.primary,
			contentDescription = "Add script",
		)
	}
}

@Composable
private fun PlayIcon(onExecuteScriptText: () -> Unit) {
	IconButton(
		onClick = onExecuteScriptText,
	) {
		Icon(
			imageVector = FeatherIcons.Play,
			tint = MaterialTheme.colorScheme.primary,
			contentDescription = "Execute script text",
		)
	}
}

@Preview
@Composable
internal fun PreviewScriptInput() {
	PreviewRenderContainer { previewData ->
		PreviewScriptInput(previewData)
	}
}

@Preview
@Composable
internal fun PreviewScriptInput(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = previewData.uiState,
	) {
		Column(
			verticalArrangement = Arrangement.SpaceBetween,
		) {
			ScriptInput(
				script = "adb devices",
				onExecuteScriptText = {},
			)
		}
	}
}
