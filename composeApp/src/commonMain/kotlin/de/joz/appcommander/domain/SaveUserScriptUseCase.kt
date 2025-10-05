package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
class SaveUserScriptUseCase(
	private val scriptsRepository: ScriptsRepository,
) {
	suspend operator fun invoke(script: ScriptsRepository.Script) = scriptsRepository.saveScript(script = script)
}
