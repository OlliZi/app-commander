package de.joz.appcommander.ui.internalpreviews

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
internal fun DarkLightPreviewContainerProvider(content: @Composable (Boolean) -> Unit) {
	Column {
		listOf(true, false).forEach {
			content(it)
		}
	}
}
