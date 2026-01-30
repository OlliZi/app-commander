package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.joz.appcommander.ui.theme.AppCommanderTheme

@Composable
fun TextLabel(
	text: String,
	textLabelType: TextLabelType,
	modifier: Modifier = Modifier,
	maxLines: Int = Int.MAX_VALUE,
	textAlign: TextAlign = TextAlign.Start,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	Text(
		text = text,
		modifier = modifier,
		maxLines = maxLines,
		textAlign = textAlign,
		overflow = overflow,
		style =
			when (textLabelType) {
				TextLabelType.BodyLarge -> MaterialTheme.typography.bodyLarge.applyThemeColor()
				TextLabelType.BodyMedium -> MaterialTheme.typography.bodyMedium.applyThemeColor()
				TextLabelType.BodySmall -> MaterialTheme.typography.bodySmall.applyThemeColor()
				TextLabelType.HeadlineLarge -> MaterialTheme.typography.headlineLarge.applyThemeColor()
				TextLabelType.HeadlineSmall -> MaterialTheme.typography.headlineSmall.applyThemeColor()
			},
	)
}

@Composable
private fun TextStyle.applyThemeColor(): TextStyle =
	copy(
		color = MaterialTheme.colorScheme.onSurface,
	)

enum class TextLabelType {
	BodyLarge,
	BodyMedium,
	BodySmall,
	HeadlineLarge,
	HeadlineSmall,
}

@Preview
@Composable
internal fun PreviewTextLabel_Dark() {
	AppCommanderTheme(
		darkTheme = true,
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
		) {
			TextLabelType.entries.forEach {
				TextLabel(
					text = it.name,
					textLabelType = it,
				)
			}
		}
	}
}

@Preview
@Composable
internal fun PreviewTextLabel_Light() {
	AppCommanderTheme(
		darkTheme = false,
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
		) {
			TextLabelType.entries.forEach {
				TextLabel(
					text = it.name,
					textLabelType = it,
				)
			}
		}
	}
}
