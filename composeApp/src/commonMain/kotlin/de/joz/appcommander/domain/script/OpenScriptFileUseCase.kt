package de.joz.appcommander.domain.script

import org.koin.core.annotation.Factory

@Factory
class OpenScriptFileUseCase(
    private val scriptsRepository: ScriptsRepository,
) {
	suspend operator fun invoke() {
		scriptsRepository.openScriptFile()
	}
}
