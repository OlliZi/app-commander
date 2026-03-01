package de.joz.appcommander.data

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.domain.script.ScriptsRepository.JsonParseResult
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

	override fun getScripts(): JsonParseResult {
		val jsonFile = File(scriptFile)

		if (!jsonFile.exists()) {
			jsonFile.writeText(text = prettyJson.encodeToString(DEFAULT_SCRIPTS))
		}

		return runCatching {
			JsonParseResult(
				scripts = prettyJson.decodeFromString<List<ScriptsRepository.Script>>(jsonFile.readText()),
				throwable = null,
			)
		}.getOrElse { error ->
			JsonParseResult(
				scripts = DEFAULT_SCRIPTS,
				throwable = error,
			)
		}
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

	override fun updateScript(
		script: ScriptsRepository.Script,
		oldScript: ScriptsRepository.Script,
	) {
		writeScriptsToFile(listOf(script) + getScripts().scripts - oldScript)
	}

	override fun saveScript(script: ScriptsRepository.Script) {
		writeScriptsToFile(listOf(script) + getScripts().scripts)
	}

	override fun removeScript(script: ScriptsRepository.Script) {
		writeScriptsToFile(getScripts().scripts - script)
	}

	private fun writeScriptsToFile(scripts: List<ScriptsRepository.Script>) {
		val jsonFile = File(scriptFile)
		jsonFile.writeText(text = prettyJson.encodeToString(scripts))
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
				ScriptsRepository.Script(
					label = "Switch dark to light to dark mode",
					script =
						"adb shell cmd uimode night no && sleep 1 && adb shell cmd uimode night yes && " +
							"sleep 1 && adb shell cmd uimode night no",
					platform = ScriptsRepository.Platform.ANDROID,
				),
			)

		internal const val JSON_FILE_NAME = "scripts.json"
	}
}
