package de.joz.appcommander.domain.script

import org.koin.core.annotation.Factory

@Factory
class SaveUserScriptsUseCase(
	private val scriptsRepository: ScriptsRepository,
) {
	operator fun invoke(scripts: List<ScriptsRepository.Script>) {
		scriptsRepository.saveScripts(scripts = scripts)
	}
}
