package de.joz.appcommander.domain.script

import org.koin.core.annotation.Factory

@Factory
class OpenScriptFileUseCase(
	private val scriptsRepository: ScriptsRepository,
) {
	operator fun invoke() {
		scriptsRepository.openScriptFile()
	}
}
