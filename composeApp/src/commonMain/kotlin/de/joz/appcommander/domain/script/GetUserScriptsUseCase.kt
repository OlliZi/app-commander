package de.joz.appcommander.domain.script

import org.koin.core.annotation.Factory

@Factory
class GetUserScriptsUseCase(
    private val scriptsRepository: ScriptsRepository,
) {
	operator fun invoke(): ScriptsRepository.JsonParseResult = scriptsRepository.getScripts()
}
