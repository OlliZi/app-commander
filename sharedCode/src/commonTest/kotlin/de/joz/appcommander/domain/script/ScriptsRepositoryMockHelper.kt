package de.joz.appcommander.domain.script

class ScriptsRepositoryMockHelper(
	private val getScriptsLambda: () -> ScriptsRepository.JsonParseResult = { TODO() },
	private val openScriptFileLambda: () -> Unit = { TODO() },
	private val updateScriptLambda: () -> ScriptsRepository.WriteScriptResult = { TODO() },
	private val saveScriptFileLambda: () -> ScriptsRepository.WriteScriptResult = { TODO() },
	private val removeScriptFileLambda: () -> ScriptsRepository.WriteScriptResult = { TODO() },
	private val getScriptFileLambda: () -> String = { TODO() },
) : ScriptsRepository {
	override fun getScripts(): ScriptsRepository.JsonParseResult = getScriptsLambda()

	override fun openScriptFile() {
		openScriptFileLambda()
	}

	override fun updateScript(
		script: ScriptsRepository.Script,
		oldScript: ScriptsRepository.Script,
	): ScriptsRepository.WriteScriptResult = updateScriptLambda()

	override fun saveScript(script: ScriptsRepository.Script): ScriptsRepository.WriteScriptResult = saveScriptFileLambda()

	override fun removeScript(script: ScriptsRepository.Script): ScriptsRepository.WriteScriptResult =
		removeScriptFileLambda()

	override fun getScriptFile(): String = getScriptFileLambda()
}
