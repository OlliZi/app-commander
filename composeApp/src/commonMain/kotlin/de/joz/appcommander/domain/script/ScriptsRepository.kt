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
		val script: String,
		val platform: Platform,
	)

	data class JsonParseResult(
		val scripts: List<Script>,
		val throwable: Throwable?,
	)

	enum class Platform(
		val label: String,
	) {
		ANDROID("Android"),
		IOS("iOS"),
	}
}
