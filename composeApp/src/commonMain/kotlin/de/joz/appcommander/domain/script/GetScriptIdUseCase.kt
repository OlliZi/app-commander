package de.joz.appcommander.domain.script

import org.koin.core.annotation.Factory

@Factory
class GetScriptIdUseCase {
	operator fun invoke(script: ScriptsRepository.Script): Int = script.hashCode()
}
