package de.joz.appcommander.data

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.domain.script.ScriptsRepository.JsonParseResult
import de.joz.appcommander.domain.script.ScriptsRepository.ParsingMetaData
import kotlinx.serialization.json.Json
import okio.FileNotFoundException
import org.koin.core.annotation.Single
import java.io.File

@JvmInline
value class ScriptFile(
	val scriptFile: String,
)

@Single
class ScriptsRepositoryImpl(
	private val addLoggingUseCase: AddLoggingUseCase,
	private val processBuilder: ProcessBuilder,
	private val scriptFile: ScriptFile,
	private val jsonHandler: Json,
) : ScriptsRepository {
	override fun getScripts(): JsonParseResult =
		runCatching {
			val jsonFile = File(scriptFile.scriptFile)
			if (!jsonFile.exists()) {
				jsonFile.writeText(text = jsonHandler.encodeToString(DEFAULT_SCRIPTS))
			}
			val scriptsFromFile = jsonFile.readText()
			val script = jsonHandler.decodeFromString<List<ScriptsRepository.Script>>(scriptsFromFile)
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

	override fun openScriptFile() {
		runCatching {
			if (File(scriptFile.scriptFile).exists().not()) {
				throw FileNotFoundException(scriptFile.scriptFile)
			}
			processBuilder.command("open", scriptFile.scriptFile)
			processBuilder.start()
		}.onFailure {
			addLoggingUseCase("Cannot open script file '${scriptFile.scriptFile}'. (Error: ${it.message})")
		}
	}

	override fun updateScript(
		script: ScriptsRepository.Script,
		oldScript: ScriptsRepository.Script,
	): ScriptsRepository.WriteScriptResult =
		runCatching {
			ScriptsRepository.WriteScriptResult.Success(writeScriptsToFile(listOf(script) + getScripts().scripts - oldScript))
		}.getOrElse { error -> ScriptsRepository.WriteScriptResult.UpdateError(message = error.message ?: "Unknown error") }

	override fun saveScript(script: ScriptsRepository.Script): ScriptsRepository.WriteScriptResult =
		runCatching {
			ScriptsRepository.WriteScriptResult.Success(writeScriptsToFile(listOf(script) + getScripts().scripts))
		}.getOrElse { error -> ScriptsRepository.WriteScriptResult.SaveError(message = error.message ?: "Unknown error") }

	override fun removeScript(script: ScriptsRepository.Script): ScriptsRepository.WriteScriptResult =
		runCatching {
			ScriptsRepository.WriteScriptResult.Success(writeScriptsToFile(getScripts().scripts - script))
		}.getOrElse { error -> ScriptsRepository.WriteScriptResult.RemoveError(message = error.message ?: "Unknown error") }

	override fun getScriptFile() = scriptFile.scriptFile

	private fun writeScriptsToFile(scripts: List<ScriptsRepository.Script>) {
		val jsonFile = File(scriptFile.scriptFile)
		jsonFile.writeText(text = jsonHandler.encodeToString(scripts))
	}

	private fun checkScriptContainsTrimmer(
		scripts: List<ScriptsRepository.Script>,
		fileJsonContent: String,
	): ParsingMetaData? =
		if (scripts.any { it.scripts.any { script -> script.contains(SCRIPT_TRIMMER) } }) {
			ParsingMetaData.MultiScriptsHint
		} else if (fileJsonContent.contains(OLD_SCRIPT_FIELD)) {
			ParsingMetaData.OldScriptFieldHint
		} else {
			null
		}

	companion object {
		val DEFAULT_SCRIPTS = listOf(
			ScriptsRepository.Script(
				label = "Dark mode",
				scripts = listOf("adb shell cmd uimode night yes"),
				platform = ScriptsRepository.Platform.ANDROID,
			),
			ScriptsRepository.Script(
				label = "Light mode",
				scripts = listOf("adb shell cmd uimode night no"),
				platform = ScriptsRepository.Platform.ANDROID,
			),
			ScriptsRepository.Script(
				label = "Switch dark to light to dark mode",
				scripts = listOf(
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
	}
}
