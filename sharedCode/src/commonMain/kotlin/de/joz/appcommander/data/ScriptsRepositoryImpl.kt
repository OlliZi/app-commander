package de.joz.appcommander.data

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.domain.script.ScriptsRepository.JsonParseResult
import de.joz.appcommander.domain.script.ScriptsRepository.ParsingMetaData
import de.joz.appcommander.domain.script.ScriptsRepository.Platform
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecodingException
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
	@OptIn(ExperimentalSerializationApi::class)
	override fun getScripts(): JsonParseResult =
		runCatching {
			val jsonFile = File(scriptFile.scriptFile)
			if (!jsonFile.exists()) {
				jsonFile.writeText(text = jsonHandler.encodeToString(DEFAULT_SCRIPTS))
			}
			val script = jsonHandler.decodeFromString<List<ScriptsRepository.Script>>(jsonFile.readText())
			JsonParseResult(
				scripts = script,
				parsingMetaData = checkScriptContainsTrimmer(script),
			)
		}.getOrElse { error ->
			if (error is JsonDecodingException && error.shortMessage.contains(SCRIPT_OBJECT_ERROR)) {
				tryMigrateToNewScriptObjects()
			} else {
				loadDefault(error)
			}
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
			val scripts = getScripts().scripts.map {
				if (it == oldScript) {
					script
				} else {
					it
				}
			}
			ScriptsRepository.WriteScriptResult.Success(writeScriptsToFile(scripts))
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

	private fun checkScriptContainsTrimmer(scripts: List<ScriptsRepository.Script>): ParsingMetaData? =
		if (scripts.any { it.scripts.any { script -> script.script.contains(SCRIPT_TRIMMER) } }) {
			ParsingMetaData.MultiScriptsHint
		} else {
			null
		}

	private fun tryMigrateToNewScriptObjects(): JsonParseResult {
		return runCatching {
			@Serializable
			data class OldScript(
				val label: String,
				val platform: Platform,
				val scripts: List<String>,
			)

			val jsonFile = File(scriptFile.scriptFile)
			val scripts = jsonHandler.decodeFromString<List<OldScript>>(jsonFile.readText())
			val migratedScripts = scripts.map { oldScriptFormat ->
				ScriptsRepository.Script(
					label = oldScriptFormat.label,
					platform = oldScriptFormat.platform,
					scripts = oldScriptFormat.scripts.map { ScriptsRepository.SubScript(script = it) },
					comment = null,
				)
			}

			jsonFile.writeText(text = jsonHandler.encodeToString(migratedScripts))

			return JsonParseResult(
				scripts = migratedScripts,
				parsingMetaData = ParsingMetaData.OldScriptFieldHint,
			)
		}.getOrElse { error ->
			loadDefault(error = error)
		}
	}

	private fun loadDefault(error: Throwable): JsonParseResult =
		JsonParseResult(
			scripts = DEFAULT_SCRIPTS,
			parsingMetaData = ParsingMetaData.ParsingError(throwable = error),
		)

	companion object {
		val DEFAULT_SCRIPTS = listOf(
			ScriptsRepository.Script(
				label = "Dark mode",
				scripts = listOf(
					ScriptsRepository.SubScript(script = "adb shell cmd uimode night yes"),
				),
				platform = Platform.ANDROID,
				comment = "Switches to dark mode",
			),
			ScriptsRepository.Script(
				label = "Light mode",
				scripts = listOf(ScriptsRepository.SubScript(script = "adb shell cmd uimode night no")),
				platform = Platform.ANDROID,
				comment = "Switches to light mode",
			),
			ScriptsRepository.Script(
				label = "Switch dark to light to dark mode",
				scripts = listOf(
					ScriptsRepository.SubScript(script = "adb shell cmd uimode night no"),
					ScriptsRepository.SubScript(script = "sleep 1"),
					ScriptsRepository.SubScript(script = "adb shell cmd uimode night yes"),
					ScriptsRepository.SubScript(script = "sleep 1"),
					ScriptsRepository.SubScript(script = "adb shell cmd uimode night no"),
				),
				platform = Platform.ANDROID,
				comment = "Switches to dark to light to dark mode",
			),
		)
		private const val SCRIPT_TRIMMER = "&&"
		const val SCRIPT_OBJECT_ERROR = "Expected start of the object '{'"
	}
}
