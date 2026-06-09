package de.joz.appcommander.domain.script

import kotlinx.serialization.Serializable

interface ScriptsRepository {
	fun getScripts(): JsonParseResult

	fun openScriptFile()

	fun updateScript(
		script: Script,
		oldScript: Script,
	): WriteScriptResult

	fun saveScript(script: Script): WriteScriptResult

	fun removeScript(script: Script): WriteScriptResult

	fun getScriptFile(): String

	@Serializable
	data class Script(
		val label: String,
		val platform: Platform,
		val scripts: List<String>,
	)

	data class JsonParseResult(
		val scripts: List<Script>,
		val parsingMetaData: ParsingMetaData?,
	)

	sealed interface ParsingMetaData {
		data class ParsingError(
			val throwable: Throwable,
		) : ParsingMetaData

		data object MultiScriptsHint : ParsingMetaData

		data object OldScriptFieldHint : ParsingMetaData
	}

	sealed interface WriteScriptResult {
		data class Success(
			val result: Unit,
		) : WriteScriptResult

		data class UpdateError(
			val message: String,
		) : WriteScriptResult

		data class SaveError(
			val message: String,
		) : WriteScriptResult

		data class RemoveError(
			val message: String,
		) : WriteScriptResult
	}

	enum class Platform(
		val label: String,
	) {
		ANDROID("Android"),
		IOS("iOS"),
		DESKTOP("Desktop"),
	}
}
