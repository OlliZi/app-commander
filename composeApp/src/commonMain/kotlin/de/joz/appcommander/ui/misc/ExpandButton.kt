package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowDown
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.ArrowRight
import compose.icons.feathericons.ArrowUp
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.theme.AppCommanderTheme

@Composable
fun ExpandButton(
	isExpanded: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	direction: ExpandButtonDirection = ExpandButtonDirection.BOTTOM_TO_TOP,
) {
	IconButton(
		modifier = modifier,
		onClick = onClick,
	) {
		Icon(
			imageVector =
				when (direction) {
					ExpandButtonDirection.BOTTOM_TO_TOP -> {
						if (isExpanded) FeatherIcons.ArrowUp else FeatherIcons.ArrowDown
					}

					ExpandButtonDirection.LEFT_TO_RIGHT -> {
						if (isExpanded) FeatherIcons.ArrowRight else FeatherIcons.ArrowLeft
					}
				},
			contentDescription = "Expand button",
			tint = MaterialTheme.colorScheme.primary,
		)
	}
}

enum class ExpandButtonDirection {
	BOTTOM_TO_TOP,
	LEFT_TO_RIGHT,
}

@Preview
@Composable
internal fun PreviewExpandButton(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = previewData.uiState,
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			ExpandButton(
				isExpanded = true,
				direction = ExpandButtonDirection.BOTTOM_TO_TOP,
				onClick = {},
			)
			ExpandButton(
				isExpanded = false,
				direction = ExpandButtonDirection.BOTTOM_TO_TOP,
				onClick = {},
			)
			ExpandButton(
				isExpanded = true,
				direction = ExpandButtonDirection.LEFT_TO_RIGHT,
				onClick = {},
			)
			ExpandButton(
				isExpanded = false,
				direction = ExpandButtonDirection.LEFT_TO_RIGHT,
				onClick = {},
			)
		}
	}
}
