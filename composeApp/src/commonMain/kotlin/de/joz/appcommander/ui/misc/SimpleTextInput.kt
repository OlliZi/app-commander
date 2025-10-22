package de.joz.appcommander.ui.misc

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
import compose.icons.feathericons.X
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SimpleTextInput(
	label: String,
	onChangeTextChange: (String) -> Unit,
) {
	var inputValue by remember { mutableStateOf("") }
	TextField(
		value = inputValue,
		modifier = Modifier.fillMaxWidth().testTag("text_field_simple_text"),
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
			onChangeTextChange(it)
		},
		placeholder = {
			Text(
				text = label,
				color = MaterialTheme.colorScheme.background,
			)
		},
		trailingIcon = {
			IconButton(
				onClick = {
					inputValue = ""
					onChangeTextChange("")
				},
			) {
				Icon(
					imageVector = FeatherIcons.X,
					tint = MaterialTheme.colorScheme.primary,
					contentDescription = "Delete script",
				)
			}
		},
	)
}

@Preview
@Composable
private fun PreviewSimpleTextInput_Dark() {
	AppCommanderTheme(
		darkTheme = true,
	) {
		SimpleTextInput(
			label = "adb devices",
			onChangeTextChange = {},
		)
	}
}

@Preview
@Composable
private fun PreviewSimpleTextInput_Light() {
	AppCommanderTheme(
		darkTheme = false,
	) {
		SimpleTextInput(
			label = "adb devices",
			onChangeTextChange = {},
		)
	}
}
