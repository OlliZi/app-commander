package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
class GetUserScriptByKeyUseCase(
	private val scriptsRepository: ScriptsRepository,
) {
	operator fun invoke(scriptKey: Int?): ScriptsRepository.Script? =
		scriptsRepository.getScripts().firstOrNull {
			it.hashCode() == scriptKey
		}
}
