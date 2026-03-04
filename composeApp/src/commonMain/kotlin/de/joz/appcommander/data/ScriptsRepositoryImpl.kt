package de.joz.appcommander.data

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.domain.script.ScriptsRepository.JsonParseResult
import de.joz.appcommander.domain.script.ScriptsRepository.ParsingMetaData
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
			val scriptsFromFile = jsonFile.readText()
			val script = prettyJson.decodeFromString<List<ScriptsRepository.Script>>(scriptsFromFile)
			val parsingMetaData = checkScriptContainsTrimmer(script, scriptsFromFile)
			JsonParseResult(
				scripts = script,
				parsingMetaData = parsingMetaData,
			)
		}.getOrElse { error ->
			JsonParseResult(
				scripts = DEFAULT_SCRIPTS,
				parsingMetaData = ParsingMetaData.ParsingError(throwable = error),
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

	private fun checkScriptContainsTrimmer(
		scripts: List<ScriptsRepository.Script>,
		fileJsonContent: String,
	): ParsingMetaData? =
		if (scripts.any { it.multiScripts.any { script -> script.contains(SCRIPT_TRIMMER) } }) {
			ParsingMetaData.MultiScriptsHint
		} else if (fileJsonContent.contains(OLD_SCRIPT_FIELD)) {
			ParsingMetaData.OldScriptFieldHint
		} else {
			null
		}

	companion object {
		private val DEFAULT_SCRIPTS =
			listOf(
				ScriptsRepository.Script(
					label = "Dark mode",
					multiScripts =
						listOf("adb shell cmd uimode night yes"),
					platform = ScriptsRepository.Platform.ANDROID,
				),
				ScriptsRepository.Script(
					label = "Light mode",
					multiScripts =
						listOf("adb shell cmd uimode night no"),
					platform = ScriptsRepository.Platform.ANDROID,
				),
				ScriptsRepository.Script(
					label = "Switch dark to light to dark mode",
					multiScripts =
						listOf(
							"adb shell cmd uimode night no",
							"sleep 1",
							"adb shell cmd uimode night yes",
							"sleep 1",
							"adb shell cmd uimode night no",
						),
					platform = ScriptsRepository.Platform.ANDROID,
				),
			)
		private const val SCRIPT_TRIMMER = "&&"
		private const val OLD_SCRIPT_FIELD = "\"script\""
		internal const val JSON_FILE_NAME = "scripts.json"
	}
}
