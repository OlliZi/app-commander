package de.joz.appcommander.ui.misc

import org.jetbrains.compose.resources.StringResource

data class TypedStringResource(
	val stringResource: StringResource,
	val substitutions: List<String>,
	val hintType: HintType = HintType.ERROR,
)

enum class HintType(
	val uiOrder: Int,
) {
	SUCCESS(uiOrder = 1),
	ERROR(uiOrder = 2),
}
