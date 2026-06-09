package de.joz.appcommander.domain.script

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SaveUserScriptUseCaseTest {
	@Test
	fun `should call save script in repository when use case is executed`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
			val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = true)
			val runFileBackupUseCaseMock: RunFileBackupUseCase = mockk(relaxed = true)

			every {
				getUserScriptByKeyUseCaseMock(any())
			} returns null

			val savePreferenceUseCase = SaveUserScriptUseCase(
				getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
				scriptsRepository = scriptsRepositoryMock,
				runFileBackupUseCase = runFileBackupUseCaseMock,
			)

			savePreferenceUseCase(
				script = ScriptsRepository.Script(
					label = "key",
					scripts = listOf("foo"),
					platform = ScriptsRepository.Platform.ANDROID,
				),
				scriptKey = null,
			)

			coVerify {
				scriptsRepositoryMock.saveScript(
					script = ScriptsRepository.Script(
						label = "key",
						scripts = listOf("foo"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
				)
				runFileBackupUseCaseMock()
			}
		}

	@Test
	fun `should call update script in repository when use case is executed`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
			val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = true)
			val runFileBackupUseCaseMock: RunFileBackupUseCase = mockk(relaxed = true)

			val newScript = ScriptsRepository.Script(
				label = "key",
				scripts = listOf("foo"),
				platform = ScriptsRepository.Platform.ANDROID,
			)
			val oldScript = ScriptsRepository.Script(
				label = "key",
				scripts = listOf("bar"),
				platform = ScriptsRepository.Platform.ANDROID,
			)

			every {
				getUserScriptByKeyUseCaseMock(oldScript.hashCode())
			} returns oldScript

			val savePreferenceUseCase = SaveUserScriptUseCase(
				getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
				scriptsRepository = scriptsRepositoryMock,
				runFileBackupUseCase = runFileBackupUseCaseMock,
			)

			savePreferenceUseCase(
				script = newScript,
				scriptKey = oldScript.hashCode(),
			)

			coVerify {
				scriptsRepositoryMock.updateScript(
					script = ScriptsRepository.Script(
						label = "key",
						scripts = listOf("foo"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
					oldScript = oldScript,
				)
				runFileBackupUseCaseMock()
			}
		}

	@Test
	fun `should return success when all sub calls are valid`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
			val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = true)
			val runFileBackupUseCaseMock: RunFileBackupUseCase = mockk(relaxed = true)

			coEvery { runFileBackupUseCaseMock.invoke() } returns RunFileBackupUseCase.Result.Success
			coEvery { scriptsRepositoryMock.saveScript(any()) } returns ScriptsRepository.WriteScriptResult.Success(Unit)
			every { getUserScriptByKeyUseCaseMock(null) } returns null

			val savePreferenceUseCase = SaveUserScriptUseCase(
				getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
				scriptsRepository = scriptsRepositoryMock,
				runFileBackupUseCase = runFileBackupUseCaseMock,
			)
			val result = savePreferenceUseCase(
				script = ScriptsRepository.Script(
					label = "key",
					scripts = listOf("foo"),
					platform = ScriptsRepository.Platform.ANDROID,
				),
				scriptKey = null,
			)

			assertIs<SaveUserScriptUseCase.Result.Success>(result)
		}

	@Test
	fun `should return error when any sub calls are invalid`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
			val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = true)
			val runFileBackupUseCaseMock: RunFileBackupUseCase = mockk(relaxed = true)

			coEvery { runFileBackupUseCaseMock.invoke() } returns RunFileBackupUseCase.Result.UnknownError("foo")
			coEvery { scriptsRepositoryMock.saveScript(any()) } returns ScriptsRepository.WriteScriptResult.SaveError("bar")
			every { getUserScriptByKeyUseCaseMock(null) } returns null

			val savePreferenceUseCase = SaveUserScriptUseCase(
				getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
				scriptsRepository = scriptsRepositoryMock,
				runFileBackupUseCase = runFileBackupUseCaseMock,
			)
			val result = savePreferenceUseCase(
				script = ScriptsRepository.Script(
					label = "key",
					scripts = listOf("foo"),
					platform = ScriptsRepository.Platform.ANDROID,
				),
				scriptKey = null,
			)

			assertIs<SaveUserScriptUseCase.Result.Error>(result)
			assertNotNull(result.backupMessage)
			assertNotNull(result.writeScriptMessage)
		}

	@Test
	fun `should return error when backup fails`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
			val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = true)
			val runFileBackupUseCaseMock: RunFileBackupUseCase = mockk(relaxed = true)

			coEvery { runFileBackupUseCaseMock.invoke() } returns RunFileBackupUseCase.Result.UnknownError("foo")
			coEvery { scriptsRepositoryMock.saveScript(any()) } returns ScriptsRepository.WriteScriptResult.Success(Unit)
			every { getUserScriptByKeyUseCaseMock(null) } returns null

			val savePreferenceUseCase = SaveUserScriptUseCase(
				getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
				scriptsRepository = scriptsRepositoryMock,
				runFileBackupUseCase = runFileBackupUseCaseMock,
			)
			val result = savePreferenceUseCase(
				script = ScriptsRepository.Script(
					label = "key",
					scripts = listOf("foo"),
					platform = ScriptsRepository.Platform.ANDROID,
				),
				scriptKey = null,
			)

			assertIs<SaveUserScriptUseCase.Result.Error>(result)
			assertNotNull(result.backupMessage)
			assertNull(result.writeScriptMessage)
		}

	@Test
	fun `should return error when writing fails`() =
		runTest {
			val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true)
			val getUserScriptByKeyUseCaseMock: GetUserScriptByKeyUseCase = mockk(relaxed = true)
			val runFileBackupUseCaseMock: RunFileBackupUseCase = mockk(relaxed = true)

			coEvery { runFileBackupUseCaseMock.invoke() } returns RunFileBackupUseCase.Result.Success
			coEvery { scriptsRepositoryMock.saveScript(any()) } returns ScriptsRepository.WriteScriptResult.SaveError("bar")
			every { getUserScriptByKeyUseCaseMock(null) } returns null

			val savePreferenceUseCase = SaveUserScriptUseCase(
				getUserScriptByKeyUseCase = getUserScriptByKeyUseCaseMock,
				scriptsRepository = scriptsRepositoryMock,
				runFileBackupUseCase = runFileBackupUseCaseMock,
			)
			val result = savePreferenceUseCase(
				script = ScriptsRepository.Script(
					label = "key",
					scripts = listOf("foo"),
					platform = ScriptsRepository.Platform.ANDROID,
				),
				scriptKey = null,
			)

			assertIs<SaveUserScriptUseCase.Result.Error>(result)
			assertNull(result.backupMessage)
			assertNotNull(result.writeScriptMessage)
		}
}
