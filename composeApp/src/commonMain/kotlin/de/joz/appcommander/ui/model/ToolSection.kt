package de.joz.appcommander.ui.model

enum class ToolSection(
	val isDefaultActive: Boolean,
) {
	FILTER(isDefaultActive = true),
	TERMINAL(isDefaultActive = true),
	LOGGING(isDefaultActive = true),
}
