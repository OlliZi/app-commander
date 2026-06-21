package de.joz.appcommander.ui.model

import androidx.compose.runtime.Stable

@Stable
sealed interface Hint {
	data class Error(
		val throwable: Throwable,
	) : Hint

	data object MultiScripts : Hint

	data object OldScriptFieldHint : Hint
}
