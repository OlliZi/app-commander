package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
class GetUserScriptsUseCase(
    private val scriptsRepository: ScriptsRepository,
) {
    suspend operator fun invoke(): List<ScriptsRepository.Script> {
        return scriptsRepository.getScripts()
    }
}