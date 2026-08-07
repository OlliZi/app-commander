package de.joz.appcommander.ui.model

import de.joz.appcommander.OsPlatform
import de.joz.appcommander.getOsPlatform
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.settings_preference_show_filter_section
import de.joz.appcommander.resources.settings_preference_show_logging_section
import de.joz.appcommander.resources.settings_preference_show_terminal_section
import org.jetbrains.compose.resources.StringResource

enum class ToolSection(
	val label: StringResource,
	val isDefaultActive: Boolean,
) {
	FILTER(
		isDefaultActive = true,
		label = Res.string.settings_preference_show_filter_section,
	),
	TERMINAL(
		isDefaultActive = when (getOsPlatform()) {
			OsPlatform.DESKTOP -> true
			OsPlatform.ANDROID -> false
			OsPlatform.IOS -> false
		},
		label = Res.string.settings_preference_show_terminal_section,
	),
	LOGGING(
		isDefaultActive = when (getOsPlatform()) {
			OsPlatform.DESKTOP -> true
			OsPlatform.ANDROID -> false
			OsPlatform.IOS -> false
		},
		label = Res.string.settings_preference_show_logging_section,
	),
}
