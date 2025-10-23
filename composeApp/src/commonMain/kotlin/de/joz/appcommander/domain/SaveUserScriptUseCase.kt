package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
class SaveUserScriptUseCase(
	private val scriptsRepository: ScriptsRepository,
	private val getUserScriptByKeyUseCase: GetUserScriptByKeyUseCase,
) {
	operator fun invoke(
		script: ScriptsRepository.Script,
		scriptKey: Int?,
	) {
		val oldScript = getUserScriptByKeyUseCase(scriptKey)
		if (oldScript != null) {
			scriptsRepository.updateScript(script = script, oldScript = oldScript)
		} else {
			scriptsRepository.saveScript(script = script)
		}
	}
}
