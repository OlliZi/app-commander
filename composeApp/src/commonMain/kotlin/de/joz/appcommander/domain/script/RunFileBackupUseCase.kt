package de.joz.appcommander.domain.script

import org.koin.core.annotation.Factory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@Factory
class RunFileBackupUseCase(
	private val scriptsRepository: ScriptsRepository,
) {
	operator fun invoke(backupStrategy: BackupStrategy) {
		runCatching {
			val backupDirectory = getBackupDirectory()
			if (checkStrategy(backupStrategy, backupDirectory)) {
				createBackupFile()
			}
		}.onFailure {
			println("Error backup the file: ${it.message}")
		}
	}

	private fun checkStrategy(
		backupStrategy: BackupStrategy,
		backupDirectory: File,
	): Boolean =
		when (backupStrategy) {
			is BackupStrategy.MaximumFiles -> backupDirectory.listFiles()?.let { it.size < backupStrategy.maxFiles } ?: true

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
		const val BACKUP_DIRECTORY = "backups"
		private const val TO_MB = 1024 * 1024
	}

	sealed interface BackupStrategy {
		data object None : BackupStrategy

		data class MaximumFiles(
			val maxFiles: Int = 100,
		) : BackupStrategy

		data class MaximumStorage(
			val maxMB: Int = 50,
		) : BackupStrategy
	}
}
