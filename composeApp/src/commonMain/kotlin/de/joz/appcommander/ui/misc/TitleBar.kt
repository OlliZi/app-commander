package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Settings
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar(
	title: String,
	onBackNavigation: (() -> Unit)? = null,
	actions: List<TitleBarAction> = emptyList(),
) {
	TopAppBar(title = {
		Text(text = title)
	}, navigationIcon = {
		if (onBackNavigation != null) {
			IconButton(
				onClick = onBackNavigation,
			) {
				Icon(
					imageVector = FeatherIcons.ArrowLeft,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary,
				)
			}
		}
	}, actions = {
		actions.forEach { actionItem ->
			IconButton(
				onClick = actionItem.action,
			) {
				Icon(
					imageVector = actionItem.icon,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary,
				)
			}
		}
	})
}

data class TitleBarAction(
	val icon: ImageVector,
	val action: () -> Unit,
)

@Preview
@Composable
internal fun PreviewTitleBar_Dark() {
	AppCommanderTheme(
		darkTheme = true,
	) {
		Column(
			verticalArrangement = Arrangement.SpaceBetween,
		) {
			TitleBar(
				title = "Title bar (plain)",
			)
			TitleBar(
				title = "Title bar with back",
				onBackNavigation = {},
			)
			TitleBar(
				title = "Title bar with back + actions",
				onBackNavigation = {},
				actions =
					listOf(
						TitleBarAction(
							action = {},
							icon = FeatherIcons.Settings,
						),
					),
			)
		}
	}
}

@Preview
@Composable
internal fun PreviewTitleBar_Light() {
	AppCommanderTheme(
		darkTheme = false,
	) {
		Column(
			verticalArrangement = Arrangement.SpaceBetween,
		) {
			TitleBar(
				title = "Title bar (plain)",
			)
			TitleBar(
				title = "Title bar with back",
				onBackNavigation = {},
			)
			TitleBar(
				title = "Title bar with back + actions",
				onBackNavigation = {},
				actions =
					listOf(
						TitleBarAction(
							action = {},
							icon = FeatherIcons.Settings,
						),
					),
			)
		}
	}
}
