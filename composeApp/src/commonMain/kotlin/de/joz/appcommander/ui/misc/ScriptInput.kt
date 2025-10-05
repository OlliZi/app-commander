package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import compose.icons.FeatherIcons
import compose.icons.feathericons.Play
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScriptInput(
	placeHolder: String,
	onExecuteScriptText: (String) -> Unit,
	onChangeScriptText: (String) -> Unit = { _ -> },
) {
	var inputValue by remember { mutableStateOf("") }
	TextField(
		value = inputValue,
		modifier = Modifier.fillMaxWidth().testTag("text_field_script_text"),
		colors =
			TextFieldDefaults.colors(
				unfocusedContainerColor = Color.White,
				focusedContainerColor = Color.White,
				focusedIndicatorColor = Color.Transparent,
				unfocusedIndicatorColor = Color.Transparent,
			),
		textStyle =
			LocalTextStyle.current.copy(
				color = MaterialTheme.colorScheme.background,
			),
		onValueChange = {
			inputValue = it
			onChangeScriptText(it)
		},
		placeholder = {
			Text(
				text = placeHolder,
				color = MaterialTheme.colorScheme.background,
			)
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
private fun PreviewScriptInput_Dark() {
	AppCommanderTheme(
		darkTheme = true,
	) {
		Column(
			verticalArrangement = Arrangement.SpaceBetween,
		) {
			ScriptInput(
				placeHolder = "adb devices",
				onExecuteScriptText = {},
			)
		}
	}
}

@Preview
@Composable
private fun PreviewScriptInput_Light() {
	AppCommanderTheme(
		darkTheme = false,
	) {
		Column(
			verticalArrangement = Arrangement.SpaceBetween,
		) {
			ScriptInput(
				placeHolder = "adb devices",
				onExecuteScriptText = {},
			)
		}
	}
}
