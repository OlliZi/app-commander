package de.joz.appcommander.ui.model

sealed interface Hint {
	data class Error(
		val throwable: Throwable,
	) : Hint

	data object MultiScripts : Hint
}
