package de.joz.appcommander.domain.script

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import org.koin.core.annotation.Factory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@Factory
class RunFileBackupUseCase(
	private val scriptsRepository: ScriptsRepository,
	private val getPreferenceUseCase: GetPreferenceUseCase,
	private val addLoggingUseCase: AddLoggingUseCase,
) {
	suspend operator fun invoke(): Result =
		runCatching {
			val backupStrategy = getBackupStrategyFromPreferences()
			val backupDirectory = getBackupDirectory()
			if (checkStrategy(backupStrategy = backupStrategy, backupDirectory = backupDirectory)) {
				createBackupFile()
			}
			Result.Success
		}.getOrElse { error ->
			addLoggingUseCase("Error backup scripts file: ${error.message}")
			when (error) {
				is Result.CannotCreateBackupDirectory -> error
				is Result.CannotCreateBackupFile -> error
				is Result.NotEnoughDiskSpaceInBackupDirectory -> error
				is Result.UnknownError -> error
				else -> Result.UnknownError(error.message ?: "Unknown error")
			}
		}

	private suspend fun getBackupStrategyFromPreferences(): BackupStrategy {
		val mb = getPreferenceUseCase.get(STORE_KEY_FOR_BACKUP_STORAGE, DEFAULT_SYSTEM_BACKUP_STORAGE_SIZE_IN_MB)
		return if (mb > 0) {
			BackupStrategy.MaximumStorage(maxMB = mb)
		} else {
			BackupStrategy.None
		}
	}

	private fun checkStrategy(
		backupStrategy: BackupStrategy,
		backupDirectory: File,
	): Boolean =
		when (backupStrategy) {
			is BackupStrategy.MaximumStorage -> {
				val mb = backupDirectory.listFiles()?.sumOf { it.length() }?.div(TO_MB) ?: 0
				if (mb <= backupStrategy.maxMB) {
					true
				} else {
					throw Result.NotEnoughDiskSpaceInBackupDirectory(
						diskSpace = mb,
						maxMB = backupStrategy.maxMB,
					)
				}
			}

			BackupStrategy.None -> {
				false
			}
		}

	private fun createBackupFile() {
		runCatching {
			val currentFile = File(scriptsRepository.getScriptFile())
			val dateFileExtension = SimpleDateFormat("_yyyy_MM_dd_HH_mm.'${currentFile.extension}'").format(Date())
			val scriptFileName = currentFile.nameWithoutExtension + dateFileExtension
			val backupDirectory = getBackupDirectory()
			backupDirectory.mkdirs()
			currentFile.copyTo(File(backupDirectory, scriptFileName), overwrite = true)
		}.getOrElse { error ->
			throw Result.CannotCreateBackupFile(error.message ?: "Unknown error")
		}
	}

	private fun getBackupDirectory(): File =
		runCatching {
			val currentFile = File(scriptsRepository.getScriptFile())
			File(currentFile.parentFile, BACKUP_DIRECTORY)
		}.getOrElse {
			throw Result.CannotCreateBackupDirectory(
				// TOOD message als string.msl nehmen
				"Cannot create backup directory. Please check your home-directory (~/.app_commander/backups).",
			)
		}

	companion object {
		const val STORE_KEY_FOR_BACKUP_STORAGE = "STORE_KEY_FOR_BACKUP_STORAGE"
		const val DEFAULT_SYSTEM_BACKUP_MAXIMUM_STORAGE_SIZE_IN_MB = 250f
		const val DEFAULT_SYSTEM_BACKUP_STORAGE_SIZE_IN_MB = 50
		const val BACKUP_DIRECTORY = "backups"
		private const val TO_MB = 1024 * 1024
	}

	sealed interface Result {
		data object Success : Result

		data class CannotCreateBackupDirectory(
			override val message: String,
		) : Exception(message),
			Result

		data class CannotCreateBackupFile(
			override val message: String,
		) : Exception(message),
			Result

		data class NotEnoughDiskSpaceInBackupDirectory(
			val diskSpace: Long,
			val maxMB: Int,
		) : Exception("There is not enough disk space for backup. Available: $diskSpace MB. Your maximum allowed: $maxMB MB"),
			Result

		data class UnknownError(
			val submessage: String,
		) : Exception("An error occurred: $submessage"),
			Result
	}

	internal sealed interface BackupStrategy {
		data object None : BackupStrategy

		data class MaximumStorage(
			val maxMB: Int = 50,
		) : BackupStrategy
	}
}
