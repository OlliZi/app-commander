package de.joz.appcommander.helper

import de.joz.appcommander.domain.script.ScriptsRepository
import java.io.File
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class ScriptsRepositoryFake(
	scripts: List<ScriptsRepository.Script>,
) : ScriptsRepository {
	private var currentScripts = scripts.toMutableList()

	var openScriptFileCounter = AtomicInt(0)
		private set

	fun getAndResetOpenScriptFileCounter(): Int {
		val counter = openScriptFileCounter.load()
		openScriptFileCounter.exchange(0)
		return counter
	}

	override fun openScriptFile() {
		openScriptFileCounter.addAndFetch(1)
	}

	override fun getScripts(): ScriptsRepository.JsonParseResult =
		ScriptsRepository.JsonParseResult(
			currentScripts.toList(),
			parsingMetaData = null,
		)

	override fun updateScript(
		script: ScriptsRepository.Script,
		oldScript: ScriptsRepository.Script,
	): ScriptsRepository.WriteScriptResult {
		currentScripts.add(script)
		currentScripts.remove(oldScript)
		currentScripts = currentScripts.distinct().toMutableList()

		return ScriptsRepository.WriteScriptResult.Success(Unit)
	}

	override fun saveScript(script: ScriptsRepository.Script): ScriptsRepository.WriteScriptResult {
		currentScripts.add(script)
		currentScripts = currentScripts.distinct().toMutableList()

		return ScriptsRepository.WriteScriptResult.Success(Unit)
	}

	override fun removeScript(script: ScriptsRepository.Script): ScriptsRepository.WriteScriptResult {
		currentScripts.remove(script)
		currentScripts = currentScripts.distinct().toMutableList()

		return ScriptsRepository.WriteScriptResult.Success(Unit)
	}

	override fun getScriptFile(): String = File("./build", "test.json").absolutePath
}
