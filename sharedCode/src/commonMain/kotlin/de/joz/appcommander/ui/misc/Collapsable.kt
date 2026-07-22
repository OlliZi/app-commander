package de.joz.appcommander.ui.misc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.scripts_filter_section_title
import de.joz.appcommander.ui.internalpreviews.DarkLightPreviewContainerProvider
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Collapsable(
	title: StringResource,
	testTag: String,
	toolbar: @Composable (Boolean) -> Unit = {},
	content: @Composable () -> Unit,
) {
	var isExpanded by rememberSaveable { mutableStateOf(false) }

	Column(
		modifier = Modifier
			.background(
				MaterialTheme.colorScheme.background,
			).padding(8.dp),
	) {
		Row(
			modifier = Modifier.height(36.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			TextLabel(
				text = stringResource(title),
				modifier = Modifier.padding(horizontal = 8.dp).weight(1f),
				textLabelType = TextLabelType.BodyLarge,
			)

			toolbar(isExpanded)

			ExpandButton(
				isExpanded = isExpanded,
				modifier = Modifier.testTag(testTag),
				onClick = { isExpanded = !isExpanded },
			)
		}
		AnimatedVisibility(visible = isExpanded) {
			content()
		}
	}
}

@Preview
@Composable
internal fun PreviewCollapsable() {
	DarkLightPreviewContainerProvider { darkMode ->
		PreviewCollapsable(darkMode)
	}
}

@Composable
internal fun PreviewCollapsable(darkMode: Boolean) {
	AppCommanderTheme(
		darkTheme = darkMode,
	) {
		Collapsable(
			title = Res.string.scripts_filter_section_title,
			testTag = "",
		) {
			TextLabel(
				text = "some content",
				textLabelType = TextLabelType.HeadlineSmall,
			)
		}
	}
}
