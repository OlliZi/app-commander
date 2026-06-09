package de.joz.appcommander.ui.misc

import org.jetbrains.compose.resources.StringResource

data class ErrorStringResource(
	val stringResource: StringResource,
	val errorSubstitutions: List<Any>,
)
