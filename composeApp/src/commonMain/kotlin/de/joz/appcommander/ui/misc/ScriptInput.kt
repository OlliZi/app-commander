package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import compose.icons.feathericons.Play
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.theme.AppCommanderTheme

@Composable
fun ScriptInput(
	onExecuteScriptText: (String) -> Unit,
	script: String = "",
	onChangeScriptText: (String) -> Unit = { _ -> },
) {
	var inputValue by remember { mutableStateOf(script) }
	TextField(
		value = inputValue,
		modifier = Modifier.fillMaxWidth().testTag("text_field_script_input"),
		colors =
			TextFieldDefaults.colors(
				unfocusedContainerColor = Color.White,
				focusedContainerColor = Color.White,
				focusedIndicatorColor = Color.Transparent,
				unfocusedIndicatorColor = Color.Transparent,
			),
		textStyle =
			TextStyle.Default.copy(
				color = Color.Black,
			),
		onValueChange = {
			inputValue = it
			onChangeScriptText(it)
		},
		trailingIcon = {
			IconButton(
				onClick = {
					onExecuteScriptText(inputValue)
				},
			) {
				Icon(
					imageVector = FeatherIcons.Play,
					tint = MaterialTheme.colorScheme.primary,
					contentDescription = "Execute script text",
				)
			}
		},
	)
}

@Preview
@Composable
internal fun PreviewScriptInput(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = false,
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
