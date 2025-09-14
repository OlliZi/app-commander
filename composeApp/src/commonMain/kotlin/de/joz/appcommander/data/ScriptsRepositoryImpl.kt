package de.joz.appcommander.data

import de.joz.appcommander.domain.ScriptsRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
@Single
class ScriptsRepositoryImpl(
    private val fileDirectory: String = getPreferenceFileStorePath(fileName = JSON_FILE_NAME)
) : ScriptsRepository {

    override fun getScripts(): List<ScriptsRepository.Script> {
        val jsonFile = File(fileDirectory)

        if (!jsonFile.exists()) {
            val prettyJson = Json {
                prettyPrint = true
                prettyPrintIndent = "    "
            }
            jsonFile.writeText(text = prettyJson.encodeToString(DEFAULT_SCRIPTS))
        }

        return runCatching {
            Json.decodeFromString<List<ScriptsRepository.Script>>(jsonFile.readText())
        }.getOrDefault(DEFAULT_SCRIPTS)
    }

    override fun openScriptFile() {
        runCatching {
            ProcessBuilder("open $fileDirectory".split(" "))
                .start()
        }.onFailure {
            println("Cannot open script file '$fileDirectory'. (Error: ${it.message})")
        }
    }

    companion object {
        private val DEFAULT_SCRIPTS = listOf(
            ScriptsRepository.Script(
                label = "Dark mode",
                script = "adb shell cmd uimode night yes",
                platform = ScriptsRepository.Platform.ANDROID,
            ),
            ScriptsRepository.Script(
                label = "Light mode",
                script = "adb shell cmd uimode night no",
                platform = ScriptsRepository.Platform.ANDROID,
            )
        )

        internal const val JSON_FILE_NAME = "scripts.json"
    }
}