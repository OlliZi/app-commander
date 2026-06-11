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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SaveUserScriptUseCaseResultMapperTest {
	private val mapper = SaveUserScriptUseCaseResultMapper()

	@Test
	fun `should map strings in case of success to empty list`() {
		assertTrue(
			mapper
				.invoke(
					SaveUserScriptUseCase.Result(
						backupMessage = null,
						writeScriptMessage = null,
					),
				).isEmpty(),
		)

		assertTrue(
			mapper
				.invoke(
					SaveUserScriptUseCase.Result(
						backupMessage = RunFileBackupUseCase.Result.Success,
						writeScriptMessage = null,
					),
				).isEmpty(),
		)
	}

	@Test
	fun `RunFileBackupUseCase - should map strings in case of error to some list`() {
		assertEquals(
			TypedStringResource(
				stringResource = Res.string.edit_error_unknown_error,
				substitutions = listOf("foo"),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.UnknownError("foo"),
			).first(),
		)

		assertEquals(
			TypedStringResource(
				stringResource = Res.string.edit_error_cannot_create_backup_file,
				substitutions = listOf("foo"),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.CannotCreateBackupFile("foo"),
			).first(),
		)

		assertEquals(
			TypedStringResource(
				stringResource = Res.string.edit_error_cannot_create_backup_directory,
				substitutions = listOf("foo"),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.CannotCreateBackupDirectory("foo"),
			).first(),
		)

		assertEquals(
			TypedStringResource(
				stringResource = Res.string.edit_error_not_enough_disk_space_in_backup_directory,
				substitutions = listOf("10", "3"),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.NotEnoughDiskSpaceInBackupDirectory(
					maxMB = 3,
					diskSpace = 10,
				),
			).first(),
		)
	}

	@Test
	fun `WriteScriptResult - should map strings in case of error to some list`() {
		assertEquals(
			TypedStringResource(
				stringResource = Res.string.edit_success_save_script,
				substitutions = emptyList(),
				hintType = HintType.SUCCESS,
			),
			mapError(
				writeScriptMessage = ScriptsRepository.WriteScriptResult.Success(Unit),
			).first(),
		)

		assertEquals(
			TypedStringResource(
				stringResource = Res.string.edit_error_save_script,
				substitutions = listOf("foo"),
			),
			mapError(
				writeScriptMessage = ScriptsRepository.WriteScriptResult.SaveError("foo"),
			).first(),
		)

		assertEquals(
			TypedStringResource(
				stringResource = Res.string.edit_error_update_script,
				substitutions = listOf("foo"),
			),
			mapError(
				writeScriptMessage = ScriptsRepository.WriteScriptResult.UpdateError("foo"),
			).first(),
		)

		assertEquals(
			TypedStringResource(
				stringResource = Res.string.edit_error_remove_script,
				substitutions = listOf("foo"),
			),
			mapError(
				writeScriptMessage = ScriptsRepository.WriteScriptResult.RemoveError("foo"),
			).first(),
		)
	}

	@Test
	fun `Mixed - should map strings in case of error to some list`() {
		assertEquals(
			listOf(
				TypedStringResource(
					stringResource = Res.string.edit_error_save_script,
					substitutions = listOf("foo"),
				),
				TypedStringResource(
					stringResource = Res.string.edit_error_cannot_create_backup_directory,
					substitutions = listOf("bar"),
				),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.CannotCreateBackupDirectory("bar"),
				writeScriptMessage = ScriptsRepository.WriteScriptResult.SaveError("foo"),
			),
		)

		assertEquals(
			listOf(
				TypedStringResource(
					stringResource = Res.string.edit_error_update_script,
					substitutions = listOf("foo"),
				),
				TypedStringResource(
					stringResource = Res.string.edit_error_cannot_create_backup_file,
					substitutions = listOf("bar"),
				),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.CannotCreateBackupFile("bar"),
				writeScriptMessage = ScriptsRepository.WriteScriptResult.UpdateError("foo"),
			),
		)

		assertEquals(
			listOf(
				TypedStringResource(
					stringResource = Res.string.edit_error_remove_script,
					substitutions = listOf("foo"),
				),
				TypedStringResource(
					stringResource = Res.string.edit_error_unknown_error,
					substitutions = listOf("bar"),
				),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.UnknownError("bar"),
				writeScriptMessage = ScriptsRepository.WriteScriptResult.RemoveError("foo"),
			),
		)

		assertEquals(
			listOf(
				TypedStringResource(
					stringResource = Res.string.edit_success_save_script,
					substitutions = emptyList(),
					hintType = HintType.SUCCESS,
				),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.Success,
				writeScriptMessage = ScriptsRepository.WriteScriptResult.Success(Unit),
			),
		)

		assertEquals(
			listOf(
				TypedStringResource(
					stringResource = Res.string.edit_success_save_script,
					substitutions = emptyList(),
					hintType = HintType.SUCCESS,
				),
				TypedStringResource(
					stringResource = Res.string.edit_error_cannot_create_backup_directory,
					substitutions = listOf("foo"),
					hintType = HintType.ERROR,
				),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.CannotCreateBackupDirectory("foo"),
				writeScriptMessage = ScriptsRepository.WriteScriptResult.Success(Unit),
			),
		)
	}

	private fun mapError(
		backupMessage: RunFileBackupUseCase.Result? = null,
		writeScriptMessage: ScriptsRepository.WriteScriptResult? = null,
	) = mapper(
		result = SaveUserScriptUseCase.Result(
			backupMessage = backupMessage,
			writeScriptMessage = writeScriptMessage,
		),
	)
}
