package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.internalpreviews.PreviewRenderContainer
import de.joz.appcommander.ui.theme.AppCommanderTheme

@Composable
fun TextLabel(
	text: String,
	textLabelType: TextLabelType,
	modifier: Modifier = Modifier,
	maxLines: Int = Int.MAX_VALUE,
	textAlign: TextAlign = TextAlign.Start,
	overflow: TextOverflow = TextOverflow.Clip,
	textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
	Text(
		text = text,
		modifier = modifier,
		maxLines = maxLines,
		textAlign = textAlign,
		overflow = overflow,
		style =
			when (textLabelType) {
				TextLabelType.BodyLarge -> MaterialTheme.typography.bodyLarge.applyThemeColor(textColor)
				TextLabelType.BodyMedium -> MaterialTheme.typography.bodyMedium.applyThemeColor(textColor)
				TextLabelType.BodySmall -> MaterialTheme.typography.bodySmall.applyThemeColor(textColor)
				TextLabelType.HeadlineLarge -> MaterialTheme.typography.headlineLarge.applyThemeColor(textColor)
				TextLabelType.HeadlineSmall -> MaterialTheme.typography.headlineSmall.applyThemeColor(textColor)
			},
	)
}

@Composable
private fun TextStyle.applyThemeColor(color: Color): TextStyle =
	copy(
		color = color,
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
internal fun PreviewTextLabel() {
	PreviewRenderContainer { previewData ->
		PreviewTextLabel(previewData)
	}
}

@Preview
@Composable
internal fun PreviewTextLabel(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = previewData.uiState,
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

			TextLabel(
				text = "some error",
				textLabelType = TextLabelType.BodyLarge,
				textColor = Color.Red,
			)
		}
	}
}
