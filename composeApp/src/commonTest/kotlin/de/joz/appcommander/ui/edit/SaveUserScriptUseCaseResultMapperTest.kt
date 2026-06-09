package de.joz.appcommander.ui.edit

import de.joz.appcommander.domain.script.RunFileBackupUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.edit_error_cannot_create_backup_directory
import de.joz.appcommander.resources.edit_error_cannot_create_backup_file
import de.joz.appcommander.resources.edit_error_not_enough_disk_space_in_backup_directory
import de.joz.appcommander.resources.edit_error_unknown_error
import de.joz.appcommander.ui.misc.ErrorStringResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SaveUserScriptUseCaseResultMapperTest {
	private val mapper = SaveUserScriptUseCaseResultMapper()

	@Test
	fun `should map strings in case of success to empty list`() {
		assertTrue(mapper.invoke(SaveUserScriptUseCase.Result.Success).isEmpty())
		assertTrue(
			mapper
				.invoke(
					SaveUserScriptUseCase.Result.Error(
						backupMessage = null,
						writeScriptMessage = null,
					),
				).isEmpty(),
		)

		assertTrue(
			mapper
				.invoke(
					SaveUserScriptUseCase.Result.Error(
						backupMessage = RunFileBackupUseCase.Result.Success,
						writeScriptMessage = null,
					),
				).isEmpty(),
		)

		assertTrue(
			mapper
				.invoke(
					SaveUserScriptUseCase.Result.Error(
						backupMessage = null,
						writeScriptMessage = ScriptsRepository.WriteScriptResult.Success(Unit),
					),
				).isEmpty(),
		)

		assertTrue(
			mapper
				.invoke(
					SaveUserScriptUseCase.Result.Error(
						backupMessage = RunFileBackupUseCase.Result.Success,
						writeScriptMessage = ScriptsRepository.WriteScriptResult.Success(Unit),
					),
				).isEmpty(),
		)
	}

	@Test
	fun `RunFileBackupUseCase - should map strings in case of error to some list`() {
		assertEquals(
			ErrorStringResource(
				stringResource = Res.string.edit_error_unknown_error,
				errorSubstitutions = listOf("foo"),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.UnknownError("foo"),
			).first(),
		)

		assertEquals(
			ErrorStringResource(
				stringResource = Res.string.edit_error_cannot_create_backup_file,
				errorSubstitutions = listOf("foo"),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.CannotCreateBackupFile("foo"),
			).first(),
		)

		assertEquals(
			ErrorStringResource(
				stringResource = Res.string.edit_error_cannot_create_backup_directory,
				errorSubstitutions = listOf("foo"),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.CannotCreateBackupDirectory("foo"),
			).first(),
		)

		assertEquals(
			ErrorStringResource(
				stringResource = Res.string.edit_error_not_enough_disk_space_in_backup_directory,
				errorSubstitutions = listOf("10", "3"),
			),
			mapError(
				backupMessage = RunFileBackupUseCase.Result.NotEnoughDiskSpaceInBackupDirectory(
					maxMB = 3,
					diskSpace = 10,
				),
			).first(),
		)
	}

	private fun mapError(
		backupMessage: RunFileBackupUseCase.Result? = null,
		writeScriptMessage: ScriptsRepository.WriteScriptResult? = null,
	) = mapper(
		result = SaveUserScriptUseCase.Result.Error(
			backupMessage = backupMessage,
			writeScriptMessage = writeScriptMessage,
		),
	)
}
