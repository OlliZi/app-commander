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
			is BackupStrategy.MaximumFiles -> !backupDirectory.exists() ||
				backupDirectory.listFiles().size < backupStrategy.maxFiles

			is BackupStrategy.MaximumStorage -> !backupDirectory.exists() || backupDirectory
				.listFiles()
				.sumOf { it.totalSpace } / 1024 < backupStrategy.maxMB

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
