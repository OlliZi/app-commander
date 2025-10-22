package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
class RemoveUserScriptUseCase(
	private val scriptsRepository: ScriptsRepository,
) {
	suspend operator fun invoke(script: ScriptsRepository.Script) = scriptsRepository.removeScript(script = script)
}
