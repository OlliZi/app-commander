package de.joz.appcommander.ui.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.edit_action_abort
import de.joz.appcommander.resources.edit_action_save
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BottomBar(
	actions: List<BottomBarAction>,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier =
			modifier
				.navigationBarsPadding()
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.background.lighter(factor = 1.1f))
				.padding(16.dp),
	) {
		actions.forEachIndexed { index, action ->
			Button(
				onClick = action.action,
			) {
				Text(
					text = stringResource(action.label),
				)
			}

			if (index != actions.lastIndex) {
				Box(modifier = Modifier.weight(1f))
			}
		}
	}
}

data class BottomBarAction(
	val label: StringResource,
	val action: () -> Unit,
)

@Preview
@Composable
private fun PreviewBottomBar_Dark() {
	AppCommanderTheme(
		darkTheme = true,
	) {
		BottomBar(
			actions =
				listOf(
					BottomBarAction(
						label = Res.string.edit_action_save,
						action = {},
					),
				),
		)
	}
}

@Preview
@Composable
private fun PreviewBottomBar_Light() {
	AppCommanderTheme(
		darkTheme = false,
	) {
		BottomBar(
			actions =
				listOf(
					BottomBarAction(
						label = Res.string.edit_action_save,
						action = {},
					),
					BottomBarAction(
						label = Res.string.edit_action_abort,
						action = {},
					),
					BottomBarAction(
						label = Res.string.edit_action_abort,
						action = {},
					),
				),
		)
	}
}
