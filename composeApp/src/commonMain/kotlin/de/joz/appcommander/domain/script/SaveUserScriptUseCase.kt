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

		val isBackupResultSuccess = backupResult is RunFileBackupUseCase.Result.Success
		val isWriteScriptResultSuccess = writeScriptResult is ScriptsRepository.WriteScriptResult.Success

		return if (isBackupResultSuccess && isWriteScriptResultSuccess) {
			Result.Success
		} else {
			Result.Error(
				backupMessage = if (isBackupResultSuccess) null else backupResult,
				writeScriptMessage = if (isWriteScriptResultSuccess) null else writeScriptResult,
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
