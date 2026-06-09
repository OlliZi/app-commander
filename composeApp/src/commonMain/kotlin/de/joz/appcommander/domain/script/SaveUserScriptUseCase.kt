package de.joz.appcommander.domain.script

import org.koin.core.annotation.Factory

@Factory
class SaveUserScriptUseCase(
	private val scriptsRepository: ScriptsRepository,
	private val getUserScriptByKeyUseCase: GetUserScriptByKeyUseCase,
	private val runFileBackupUseCase: RunFileBackupUseCase,
) {
	suspend operator fun invoke(
		script: ScriptsRepository.Script,
		scriptKey: Int?,
	): Result {
		val backupResult = runFileBackupUseCase()

		val oldScript = getUserScriptByKeyUseCase(scriptKey)
		val writeScriptResult = if (oldScript != null) {
			scriptsRepository.updateScript(script = script, oldScript = oldScript)
		} else {
			scriptsRepository.saveScript(script = script)
		}

		return if (backupResult is RunFileBackupUseCase.Result.Success &&
			writeScriptResult is ScriptsRepository.WriteScriptResult.Success
		) {
			Result.Success
		} else {
			Result.Error(
				backupMessage = backupResult,
				writeScriptMessage = writeScriptResult,
			)
		}
	}

	sealed interface Result {
		data object Success : Result

		data class Error(
			val backupMessage: RunFileBackupUseCase.Result?,
			val writeScriptMessage: ScriptsRepository.WriteScriptResult?,
		) : Result
	}
}
