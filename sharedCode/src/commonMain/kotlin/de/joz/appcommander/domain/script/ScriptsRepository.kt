package de.joz.appcommander.domain.script

import de.joz.appcommander.data.json.ScriptCodeSerializer
import de.joz.appcommander.data.json.ScriptSerializer
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

	data class JsonParseResult(
		val scripts: List<Script>,
		val parsingMetaData: ParsingMetaData?,
	)

	@Serializable(with = ScriptSerializer::class)
	data class Script(
		val label: String,
		val platform: Platform,
		val scripts: List<ScriptCode>,
		val comment: String? = null,
	)

	@Serializable(with = ScriptCodeSerializer::class)
	sealed interface ScriptCode {
		val script: String

		@Serializable
		data class Script(
			override val script: String,
		) : ScriptCode

		@Serializable
		data class CommentedScript(
			override val script: String,
			val comment: String,
		) : ScriptCode
	}

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
