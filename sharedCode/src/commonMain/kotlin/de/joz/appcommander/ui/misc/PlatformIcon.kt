package de.joz.appcommander.ui.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.android
import de.joz.appcommander.resources.desktop
import de.joz.appcommander.resources.ios
import de.joz.appcommander.ui.internalpreviews.DarkLightPreviewContainerProvider
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlatformIcon(
	isActive: Boolean,
	platform: ScriptsRepository.Platform,
) {
	Box(
		modifier = Modifier
			.size(40.dp)
			.background(
				if (isActive) Color.Unspecified else Color.White,
				CircleShape,
			).padding(platform.padding()),
	) {
		Icon(
			modifier = Modifier.fillMaxSize(),
			painter = painterResource(platform.icon()),
			contentDescription = null,
			tint = Color.Black,
		)
	}
}

private fun ScriptsRepository.Platform.padding(): Dp =
	when (this) {
		ScriptsRepository.Platform.ANDROID -> 1.dp
		ScriptsRepository.Platform.IOS -> 5.dp
		ScriptsRepository.Platform.DESKTOP -> 7.dp
	}

private fun ScriptsRepository.Platform.icon(): DrawableResource =
	when (this) {
		ScriptsRepository.Platform.ANDROID -> Res.drawable.android
		ScriptsRepository.Platform.IOS -> Res.drawable.ios
		ScriptsRepository.Platform.DESKTOP -> Res.drawable.desktop
	}

@Preview
@Composable
internal fun PreviewPlatformIcon() {
	DarkLightPreviewContainerProvider { darkMode ->
		PreviewPlatformIcon(darkMode)
	}
}

@Composable
internal fun PreviewPlatformIcon(darkMode: Boolean) {
	AppCommanderTheme(
		darkTheme = darkMode,
	) {
		ScriptsRepository.Platform.entries.forEach {
			PlatformIcon(
				isActive = true,
				platform = it,
			)
			PlatformIcon(
				isActive = false,
				platform = it,
			)
		}
	}
}
