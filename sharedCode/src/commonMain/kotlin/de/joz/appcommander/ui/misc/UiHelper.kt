package de.joz.appcommander.ui.misc

import de.joz.appcommander.domain.script.ScriptsRepository

object UiHelper {
	fun isScriptExecutableByUi(
		isAtMinimumOneDeviceSelected: Boolean,
		platform: ScriptsRepository.Platform,
	): Boolean = isAtMinimumOneDeviceSelected || platform == ScriptsRepository.Platform.DESKTOP
}
