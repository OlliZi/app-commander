package de.joz.appcommander.data

import de.joz.appcommander.domain.ScriptsRepository
import de.joz.appcommander.domain.logging.AddLoggingUseCase
import kotlinx.serialization.json.Json
import okio.FileNotFoundException
import org.koin.core.annotation.Single
import java.io.File

@Single
class ScriptsRepositoryImpl(
	private val addLoggingUseCase: AddLoggingUseCase,
	private val scriptFile: String = getPreferenceFileStorePath(fileName = JSON_FILE_NAME),
	private val processBuilder: ProcessBuilder = ProcessBuilder(),
) : ScriptsRepository {
	private val prettyJson =
		Json {
			prettyPrint = true
			ignoreUnknownKeys = true
		}

	override fun getScripts(): List<ScriptsRepository.Script> {
		val jsonFile = File(scriptFile)

		if (!jsonFile.exists()) {
			jsonFile.writeText(text = prettyJson.encodeToString(DEFAULT_SCRIPTS))
		}

		return runCatching {
			prettyJson.decodeFromString<List<ScriptsRepository.Script>>(jsonFile.readText())
		}.getOrDefault(DEFAULT_SCRIPTS)
	}

	override fun openScriptFile() {
		runCatching {
			if (File(scriptFile).exists().not()) {
				throw FileNotFoundException(scriptFile)
			}
			processBuilder.command("open", scriptFile)
			processBuilder.start()
		}.onFailure {
			addLoggingUseCase("Cannot open script file '$scriptFile'. (Error: ${it.message})")
		}
	}

	override fun saveScript(script: ScriptsRepository.Script) {
		val jsonFile = File(scriptFile)
		val newScripts = listOf(script) + getScripts()

		jsonFile.writeText(text = prettyJson.encodeToString(newScripts))
	}

	override fun removeScript(script: ScriptsRepository.Script) {
		val jsonFile = File(scriptFile)
		val newScripts = getScripts() - script

		jsonFile.writeText(text = prettyJson.encodeToString(newScripts))
	}

	companion object {
		private val DEFAULT_SCRIPTS =
			listOf(
				ScriptsRepository.Script(
					label = "Dark mode",
					script = "adb shell cmd uimode night yes",
					platform = ScriptsRepository.Platform.ANDROID,
				),
				ScriptsRepository.Script(
					label = "Light mode",
					script = "adb shell cmd uimode night no",
					platform = ScriptsRepository.Platform.ANDROID,
				),
			)

		internal const val JSON_FILE_NAME = "scripts.json"
	}
}
