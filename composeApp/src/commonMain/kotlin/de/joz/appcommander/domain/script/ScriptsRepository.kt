package de.joz.appcommander.domain.script

import kotlinx.serialization.Serializable

interface ScriptsRepository {
	fun getScripts(): JsonParseResult

	fun openScriptFile()

	fun updateScript(
		script: Script,
		oldScript: Script,
	)

	fun saveScript(script: Script)

	fun removeScript(script: Script)

	@Serializable
	data class Script(
		val label: String,
		val platform: Platform,
		val multiScripts: List<String>,
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
	}

	enum class Platform(
		val label: String,
	) {
		ANDROID("Android"),
		IOS("iOS"),
	}
}
