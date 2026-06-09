package de.joz.appcommander.domain.script

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.script.RunFileBackupUseCase.Companion.DEFAULT_SYSTEM_BACKUP_STORAGE_SIZE_IN_MB
import de.joz.appcommander.domain.script.RunFileBackupUseCase.Companion.STORE_KEY_FOR_BACKUP_STORAGE
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RunFileBackupUseCaseTest {
	private val testFile = File("./build", "test.json")
	private val addLoggingUseCaseMock: AddLoggingUseCase = mockk(relaxed = true)
	private val getPreferenceUseCaseMock: GetPreferenceUseCase = mockk(relaxed = true)
	private val scriptsRepositoryMock: ScriptsRepository = mockk(relaxed = true) {
		every { getScriptFile() } returns testFile.absolutePath
	}

	@BeforeTest
	fun setUp() {
		testFile.delete()
		testFile.writeText("test-content")
		getBackupDirectory()?.deleteRecursively()
	}

	@Test
	fun `should do nothing when strategy is None`() =
		runTest {
			val contentBefore = testFile.readText()
			coEvery {
				getPreferenceUseCaseMock.get(
					STORE_KEY_FOR_BACKUP_STORAGE,
					any<Int>(),
				)
			} returns -1

			val result = createUseCase().invoke()

			assertIs<RunFileBackupUseCase.Result.Success>(result)
			assertEquals(contentBefore, testFile.readText())
			assertTrue(getBackupDirectory()?.listFiles().orEmpty().isEmpty())
			coVerify { addLoggingUseCaseMock wasNot called }
		}

	@Test
	fun `should do a backup when strategy is MaximumStorage`() =
		runTest {
			val contentBefore = testFile.readText()

			coEvery {
				getPreferenceUseCaseMock.get(
					STORE_KEY_FOR_BACKUP_STORAGE,
					any<Int>(),
				)
			} returns 1

			val result = createUseCase().invoke()

			assertIs<RunFileBackupUseCase.Result.Success>(result)
			assertEquals(contentBefore, testFile.readText())
			assertEquals(1, getBackupDirectory()?.listFiles().orEmpty().size)
			assertEquals(
				contentBefore,
				getBackupDirectory()?.listFiles()?.first()?.readText(),
			)
		}

	@Test
	fun `should do no backup when strategy is MaximumStorage but disk space is not sufficient`() =
		runTest {
			coEvery {
				getPreferenceUseCaseMock.get(
					STORE_KEY_FOR_BACKUP_STORAGE,
					any<Int>(),
				)
			} returns 3
			val useCase = createUseCase()

			writeBigFile(mb = 1, testFile)
			val contentBefore = testFile.readText()

			assertEquals(0, getBackupDirectory()?.listFiles().orEmpty().size)

			createBackupDirectory()
			writeBigFile(mb = 1, File(getBackupDirectory(), "test_file1.json"))
			writeBigFile(mb = 1, File(getBackupDirectory(), "test_file2.json"))

			assertEquals(contentBefore, testFile.readText())
			assertEquals(2, getBackupDirectory()?.listFiles().orEmpty().size)
			assertEquals(
				contentBefore,
				getBackupDirectory()?.listFiles()?.get(0)?.readText(),
			)
			assertEquals(
				contentBefore,
				getBackupDirectory()?.listFiles()?.get(1)?.readText(),
			)

			val result = useCase.invoke()

			assertIs<RunFileBackupUseCase.Result.Success>(result)
			assertEquals(contentBefore, testFile.readText())
			assertEquals(3, getBackupDirectory()?.listFiles().orEmpty().size)
			assertEquals(
				contentBefore,
				getBackupDirectory()?.listFiles()?.get(0)?.readText(),
			)
			assertEquals(
				contentBefore,
				getBackupDirectory()?.listFiles()?.get(1)?.readText(),
			)
			assertEquals(
				contentBefore,
				getBackupDirectory()?.listFiles()?.get(2)?.readText(),
			)
		}

	@Test
	fun `should log error when an exception occurred`() =
		runTest {
			every { scriptsRepositoryMock.getScriptFile() } throws IllegalArgumentException("test error")

			val result = createUseCase().invoke()

			assertIs<RunFileBackupUseCase.Result.CannotCreateBackupDirectory>(result)
			assertEquals(
				"Cannot create backup directory. Please check your home-directory (~/.app_commander/backups).",
				result.message,
			)

			verify {
				addLoggingUseCaseMock.invoke(
					"Error backup scripts file: Cannot create backup directory. Please check your home-directory (~/.app_commander/backups).",
				)
			}
		}

	@Test
	fun `should log error when strategy cannot read from preferences`() =
		runTest {
			coEvery {
				getPreferenceUseCaseMock.get(
					STORE_KEY_FOR_BACKUP_STORAGE,
					DEFAULT_SYSTEM_BACKUP_STORAGE_SIZE_IN_MB,
				)
			} throws IllegalArgumentException("test error")

			val result = createUseCase().invoke()

			assertIs<RunFileBackupUseCase.Result.UnknownError>(result)
			assertEquals(
				"An error occurred: test error",
				result.message,
			)

			verify {
				addLoggingUseCaseMock.invoke(
					"Error backup scripts file: test error",
				)
			}
		}

	@Test
	fun `should log error when backup directory cannot created`() =
		runTest {
			coEvery {
				scriptsRepositoryMock.getScriptFile()
			} throws IllegalArgumentException("foo")

			val result = createUseCase().invoke()

			assertIs<RunFileBackupUseCase.Result.CannotCreateBackupDirectory>(result)
			assertEquals(
				"Cannot create backup directory. Please check your home-directory (~/.app_commander/backups).",
				result.message,
			)

			verify {
				addLoggingUseCaseMock.invoke(
					"Error backup scripts file: Cannot create backup directory. Please check your home-directory (~/.app_commander/backups).",
				)
			}
		}

	private fun writeBigFile(
		mb: Int,
		testFile: File,
	) {
		val sizeInBytes = mb * 1024 * 1024
		testFile.writeBytes(ByteArray(sizeInBytes))
	}

	private fun getBackupDirectory() =
		testFile.parentFile
			.listFiles()
			.firstOrNull { it.isDirectory && it.nameWithoutExtension == RunFileBackupUseCase.BACKUP_DIRECTORY }

	private fun createBackupDirectory() {
		File(testFile.parentFile, RunFileBackupUseCase.BACKUP_DIRECTORY).mkdirs()
	}

	private fun createUseCase() =
		RunFileBackupUseCase(
			scriptsRepository = scriptsRepositoryMock,
			getPreferenceUseCase = getPreferenceUseCaseMock,
			addLoggingUseCase = addLoggingUseCaseMock,
		)
}
