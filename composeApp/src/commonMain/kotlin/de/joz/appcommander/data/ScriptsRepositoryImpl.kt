package de.joz.appcommander.data

import de.joz.appcommander.domain.ScriptsRepository
import org.koin.core.annotation.Single

@Single
class ScriptsRepositoryImpl : ScriptsRepository {

    override fun getScripts(): List<ScriptsRepository.Script> {
        return DEFAULT_SCRIPTS
    }

    companion object {
        private val DEFAULT_SCRIPTS = listOf(
            ScriptsRepository.Script(
                label = "Dark mode",
                script = "adb shell...",
                platform = ScriptsRepository.Platform.ANDROID,
            )
        )
    }
}