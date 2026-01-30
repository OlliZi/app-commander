package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.joz.appcommander.domain.ScriptsRepository
import de.joz.appcommander.ui.theme.AppCommanderTheme

@Composable
fun PlatformSelection(
	selectedPlatform: ScriptsRepository.Platform,
	onSelectPlatform: (ScriptsRepository.Platform) -> Unit,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(16.dp),
	) {
		ScriptsRepository.Platform.entries.forEach { platform ->
			LabelledSwitch(
				label = platform.label,
				checked = selectedPlatform == platform,
				onCheckedChange = {
					onSelectPlatform(platform)
				},
			)
		}
	}
}

@Preview
@Composable
internal fun PreviewPlatformSelection_Dark() {
	AppCommanderTheme(
		darkTheme = true,
	) {
		PlatformSelection(
			selectedPlatform = ScriptsRepository.Platform.ANDROID,
			onSelectPlatform = { _ -> },
		)
	}
}

@Preview
@Composable
internal fun PreviewPlatformSelection_Light() {
	AppCommanderTheme(
		darkTheme = false,
	) {
		PlatformSelection(
			selectedPlatform = ScriptsRepository.Platform.IOS,
			onSelectPlatform = { _ -> },
		)
	}
}
