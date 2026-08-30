package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.X
import de.joz.appcommander.ui.internalpreviews.DarkLightPreviewContainerProvider
import de.joz.appcommander.ui.theme.AppCommanderTheme

@Composable
fun SimpleTextInput(
	value: String = "",
	onChangeTextChange: (String) -> Unit,
	testTag: String = "text_field_simple_text",
	modifier: Modifier = Modifier,
) {
	var inputValue by remember { mutableStateOf(value) }
	TextField(
		shape = RoundedCornerShape(10.dp),
		value = inputValue,
		modifier = modifier.fillMaxWidth().testTag(testTag),
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
			onChangeTextChange(it)
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
internal fun PreviewSimpleTextInput() {
	DarkLightPreviewContainerProvider { darkMode ->
		PreviewSimpleTextInput(darkMode)
	}
}

@Composable
internal fun PreviewSimpleTextInput(darkMode: Boolean) {
	AppCommanderTheme(
		darkTheme = darkMode,
	) {
		SimpleTextInput(
			value = "adb devices",
			onChangeTextChange = {},
		)
	}
}
