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
import de.joz.appcommander.resources.edit_success_save_script
import de.joz.appcommander.ui.misc.HintType
import de.joz.appcommander.ui.misc.TypedStringResource
import org.koin.core.annotation.Factory

@Factory
class SaveUserScriptUseCaseResultMapper {
	operator fun invoke(result: SaveUserScriptUseCase.Result): List<TypedStringResource> =
		buildList {
			mapWriteScriptResult(result.writeScriptMessage)?.let { add(it) }
			mapBackupResult(result.backupMessage)?.let { add(it) }
		}.sortedBy { it.hintType.uiOrder }

	private fun mapWriteScriptResult(result: ScriptsRepository.WriteScriptResult?): TypedStringResource? =
		when (result) {
			is ScriptsRepository.WriteScriptResult.RemoveError -> {
				TypedStringResource(
					stringResource = Res.string.edit_error_remove_script,
					substitutions = listOf(result.message),
				)
			}

			is ScriptsRepository.WriteScriptResult.SaveError -> {
				TypedStringResource(
					stringResource = Res.string.edit_error_save_script,
					substitutions = listOf(result.message),
				)
			}

			is ScriptsRepository.WriteScriptResult.UpdateError -> {
				TypedStringResource(
					stringResource = Res.string.edit_error_update_script,
					substitutions = listOf(result.message),
				)
			}

			is ScriptsRepository.WriteScriptResult.Success -> {
				TypedStringResource(
					stringResource = Res.string.edit_success_save_script,
					substitutions = emptyList(),
					hintType = HintType.SUCCESS,
				)
			}

			null -> {
				null
			}
		}

	private fun mapBackupResult(result: RunFileBackupUseCase.Result?): TypedStringResource? =
		when (result) {
			is RunFileBackupUseCase.Result.CannotCreateBackupDirectory -> {
				TypedStringResource(
					stringResource = Res.string.edit_error_cannot_create_backup_directory,
					substitutions = listOf(result.message),
				)
			}

			is RunFileBackupUseCase.Result.CannotCreateBackupFile -> {
				TypedStringResource(
					stringResource = Res.string.edit_error_cannot_create_backup_file,
					substitutions = listOf(result.message),
				)
			}

			is RunFileBackupUseCase.Result.NotEnoughDiskSpaceInBackupDirectory -> {
				TypedStringResource(
					stringResource = Res.string.edit_error_not_enough_disk_space_in_backup_directory,
					substitutions = listOf(
						result.diskSpace.toString(),
						result.maxMB.toString(),
					),
				)
			}

			is RunFileBackupUseCase.Result.UnknownError -> {
				TypedStringResource(
					stringResource = Res.string.edit_error_unknown_error,
					substitutions = listOf(result.message ?: "Unknown error"),
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
