package de.joz.appcommander.ui.edit

import de.joz.appcommander.domain.script.RunFileBackupUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.edit_error_cannot_create_backup_directory
import de.joz.appcommander.resources.edit_error_cannot_create_backup_file
import de.joz.appcommander.resources.edit_error_not_enough_disk_space_in_backup_directory
import de.joz.appcommander.resources.edit_error_remove_script
import de.joz.appcommander.resources.edit_error_save_script
import de.joz.appcommander.resources.edit_error_unknown_error
import de.joz.appcommander.resources.edit_error_update_script
import de.joz.appcommander.ui.misc.ErrorStringResource
import org.koin.core.annotation.Factory

@Factory
class SaveUserScriptUseCaseResultMapper {
	operator fun invoke(result: SaveUserScriptUseCase.Result): List<ErrorStringResource> =
		when (result) {
			is SaveUserScriptUseCase.Result.Success -> {
				emptyList()
			}

			is SaveUserScriptUseCase.Result.Error -> {
				buildList {
					mapBackupResult(result)?.let { add(it) }
					mapWriteScriptResult(result)?.let { add(it) }
				}
			}
		}

	private fun mapWriteScriptResult(result: SaveUserScriptUseCase.Result.Error): ErrorStringResource? =
		when (result.writeScriptMessage) {
			is ScriptsRepository.WriteScriptResult.RemoveError -> {
				ErrorStringResource(
					stringResource = Res.string.edit_error_remove_script,
					errorSubstitutions = listOf(result.writeScriptMessage.message),
				)
			}

			is ScriptsRepository.WriteScriptResult.SaveError -> {
				ErrorStringResource(
					stringResource = Res.string.edit_error_save_script,
					errorSubstitutions = listOf(result.writeScriptMessage.message),
				)
			}

			is ScriptsRepository.WriteScriptResult.UpdateError -> {
				ErrorStringResource(
					stringResource = Res.string.edit_error_update_script,
					errorSubstitutions = listOf(result.writeScriptMessage.message),
				)
			}

			is ScriptsRepository.WriteScriptResult.Success -> {
				null
			}

			null -> {
				null
			}
		}

	private fun mapBackupResult(result: SaveUserScriptUseCase.Result.Error): ErrorStringResource? =
		when (result.backupMessage) {
			is RunFileBackupUseCase.Result.CannotCreateBackupDirectory -> {
				ErrorStringResource(
					stringResource = Res.string.edit_error_cannot_create_backup_directory,
					errorSubstitutions = listOf(result.backupMessage.message),
				)
			}

			is RunFileBackupUseCase.Result.CannotCreateBackupFile -> {
				ErrorStringResource(
					stringResource = Res.string.edit_error_cannot_create_backup_file,
					errorSubstitutions = listOf(result.backupMessage.message),
				)
			}

			is RunFileBackupUseCase.Result.NotEnoughDiskSpaceInBackupDirectory -> {
				ErrorStringResource(
					stringResource = Res.string.edit_error_not_enough_disk_space_in_backup_directory,
					errorSubstitutions = listOf(
						result.backupMessage.diskSpace.toString(),
						result.backupMessage.maxMB.toString(),
					),
				)
			}

			is RunFileBackupUseCase.Result.UnknownError -> {
				ErrorStringResource(
					stringResource = Res.string.edit_error_unknown_error,
					errorSubstitutions = listOf(result.backupMessage.message ?: "Unknown error"),
				)
			}

			RunFileBackupUseCase.Result.Success -> {
				null
			}

			null -> {
				null
			}
		}
}
