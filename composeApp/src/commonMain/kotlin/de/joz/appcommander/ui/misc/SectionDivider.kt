package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SectionDivider() {
	HorizontalDivider(
		modifier = Modifier.padding(vertical = 16.dp),
	)
}

@Preview
@Composable
private fun PreviewSectionDivider_Dark() {
	AppCommanderTheme(
		darkTheme = true,
	) {
		SectionDivider()
	}
}

@Preview
@Composable
private fun PreviewSectionDivider_Light() {
	AppCommanderTheme(
		darkTheme = false,
	) {
		SectionDivider()
	}
}
