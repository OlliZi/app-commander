package de.joz.appcommander.domain

import kotlinx.serialization.Serializable

interface ScriptsRepository {
	fun getScripts(): List<Script>

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

	enum class Platform(
		val label: String,
	) {
		ANDROID("Android"),
		IOS("iOS"),
	}
}
