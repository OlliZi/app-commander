package de.joz.appcommander.ui.misc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.theme.AppCommanderTheme

@Composable
fun LabelledSwitch(
	label: String,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	textModifier: Modifier = Modifier,
) {
	val interactionSource = remember { MutableInteractionSource() }
	Row(
		modifier =
			modifier
				.clickable(
					interactionSource = interactionSource,
					indication = null,
					role = Role.Switch,
					onClick = {
						onCheckedChange(!checked)
					},
				),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		TextLabel(
			text = label,
			modifier = textModifier,
			textLabelType = TextLabelType.BodyLarge,
		)

		Switch(
			colors =
				SwitchDefaults.colors(
					checkedThumbColor = MaterialTheme.colorScheme.primary,
					checkedTrackColor = Color.LightGray,
				),
			modifier = Modifier.align(Alignment.CenterVertically),
			checked = checked,
			onCheckedChange = {
				onCheckedChange(it)
			},
		)
	}
}

@Preview
@Composable
internal fun PreviewLabelledSwitch(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = previewData.uiState,
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
		) {
			LabelledSwitch(
				label = "some switch ON",
				checked = true,
				onCheckedChange = {},
			)
			LabelledSwitch(
				label = "some switch OFF",
				checked = false,
				onCheckedChange = {},
			)
		}
	}
}
