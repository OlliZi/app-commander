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

		val isBackupSuccess = backupResult is RunFileBackupUseCase.Result.Success
		val writeScriptResultSuccess = writeScriptResult is ScriptsRepository.WriteScriptResult.Success

		return Result(
			backupMessage = if (isBackupSuccess) null else backupResult,
			writeScriptMessage = if (isBackupSuccess && writeScriptResultSuccess) null else writeScriptResult,
		)
	}

	data class Result(
		val backupMessage: RunFileBackupUseCase.Result?,
		val writeScriptMessage: ScriptsRepository.WriteScriptResult?,
	)
}
