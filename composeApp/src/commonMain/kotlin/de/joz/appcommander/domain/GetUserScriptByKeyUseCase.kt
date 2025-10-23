package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
class GetUserScriptByKeyUseCase(
	private val scriptsRepository: ScriptsRepository,
	private val getScriptIdUseCase: GetScriptIdUseCase,
) {
	operator fun invoke(scriptKey: Int?): ScriptsRepository.Script? =
		scriptsRepository.getScripts().firstOrNull {
			getScriptIdUseCase(it) == scriptKey
		}
}
