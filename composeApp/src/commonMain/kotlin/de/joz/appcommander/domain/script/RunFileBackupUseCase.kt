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
	suspend operator fun invoke() {
		runCatching {
			val backupStrategy = getBackupStrategyFromPreferences()
			val backupDirectory = getBackupDirectory()
			if (checkStrategy(backupStrategy, backupDirectory)) {
				createBackupFile()
			}
		}.onFailure {
			// write tet for this case
			addLoggingUseCase("Error backup the file: ${it.message}")
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
			is BackupStrategy.MaximumStorage -> backupDirectory.listFiles()?.let { files ->
				val mb = files.sumOf { it.length() } / TO_MB
				mb <= backupStrategy.maxMB
			} ?: true

			BackupStrategy.None -> false
		}

	private fun createBackupFile() {
		runCatching {
			val currentFile = File(scriptsRepository.getScriptFile())
			val dateFileExtension = SimpleDateFormat("_yyyy_MM_dd_HH_mm.'${currentFile.extension}'").format(Date())
			val scriptFileName = currentFile.nameWithoutExtension + dateFileExtension
			val backupDirectory = getBackupDirectory()
			backupDirectory.mkdirs()
			currentFile.copyTo(File(backupDirectory, scriptFileName), overwrite = true)
		}
	}

	private fun getBackupDirectory(): File {
		val currentFile = File(scriptsRepository.getScriptFile())
		return File(currentFile.parentFile, BACKUP_DIRECTORY)
	}

	companion object {
		const val STORE_KEY_FOR_BACKUP_STORAGE = "STORE_KEY_FOR_BACKUP_STORAGE"
		const val DEFAULT_SYSTEM_BACKUP_MAXIMUM_STORAGE_SIZE_IN_MB = 250f
		const val DEFAULT_SYSTEM_BACKUP_STORAGE_SIZE_IN_MB = 50
		const val BACKUP_DIRECTORY = "backups"
		private const val TO_MB = 1024 * 1024
	}

	internal sealed interface BackupStrategy {
		data object None : BackupStrategy

		data class MaximumStorage(
			val maxMB: Int = 50,
		) : BackupStrategy
	}
}
