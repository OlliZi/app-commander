package de.joz.appcommander.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class TrackScriptsFileChangesUseCase(
    private val getUserScriptsUseCase: GetUserScriptsUseCase,
) {
    operator fun invoke(): Flow<List<ScriptsRepository.Script>> {
        return flow {
            var scripts: List<ScriptsRepository.Script>? = null
            while (true) {
                delay(1000)

                val newLoadedScripts = getUserScriptsUseCase()

                if (scripts == null) {
                    scripts = newLoadedScripts
                } else if (scripts != newLoadedScripts) {
                    scripts = newLoadedScripts
                    emit(newLoadedScripts)
                }
            }
        }
    }
}