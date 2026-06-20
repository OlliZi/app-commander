package de.joz.appcommander.domain.script

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.script.RunFileBackupUseCase.Companion.DEFAULT_SYSTEM_BACKUP_STORAGE_SIZE_IN_MB
import de.joz.appcommander.domain.script.RunFileBackupUseCase.Companion.STORE_KEY_FOR_BACKUP_STORAGE
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
			assertTrue(getAllFilesFromBackupDirectory().isEmpty())
			coVerify(exactly = 0) { addLoggingUseCaseMock.invoke(any()) }
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
			} returns 2

			val result = createUseCase().invoke()

			assertIs<RunFileBackupUseCase.Result.Success>(result)
			assertEquals(contentBefore, testFile.readText())

			val files = getAllFilesFromBackupDirectory()
			assertEquals(1, files.size)
			assertEquals(
				contentBefore,
				files.first().readText(),
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

			write1MBFile(testFile)
			val contentBefore = testFile.readText()

			createBackupDirectory()
			assertEquals(0, getAllFilesFromBackupDirectory().size)

			write1MBFile(File(getBackupDirectory(), "test_file1.json"))
			write1MBFile(File(getBackupDirectory(), "test_file2.json"))

			assertEquals(contentBefore, testFile.readText())
			assertEquals(2, getAllFilesFromBackupDirectory().size)
			assertEquals(
				contentBefore,
				getAllFilesFromBackupDirectory()[0].readText(),
			)
			assertEquals(
				contentBefore,
				getAllFilesFromBackupDirectory()[1].readText(),
			)

			val result1 = useCase.invoke()

			val files = getAllFilesFromBackupDirectory()
			assertIs<RunFileBackupUseCase.Result.Success>(result1)
			assertEquals(contentBefore, testFile.readText())
			assertEquals(3, files.count())
			assertEquals(
				contentBefore,
				files[0].readText(),
			)
			assertEquals(
				contentBefore,
				files[1].readText(),
			)
			assertEquals(
				contentBefore,
				files[2].readText(),
			)

			val result2 = useCase.invoke()

			assertEquals(contentBefore, testFile.readText())
			assertEquals(3, getAllFilesFromBackupDirectory().size)
			assertIs<RunFileBackupUseCase.Result.NotEnoughDiskSpaceInBackupDirectory>(result2)

			assertEquals(
				"Not enough disk space.",
				result2.message,
			)

			assertEquals(4, result2.diskSpace)
			assertEquals(3, result2.maxMB)

			verify {
				addLoggingUseCaseMock.invoke(
					"Error backup scripts file: Not enough disk space.",
				)
			}
		}

	@Test
	fun `should log error when an exception occurred`() =
		runTest {
			every { scriptsRepositoryMock.getScriptFile() } throws IllegalArgumentException("test error")

			val result = createUseCase().invoke()

			assertIs<RunFileBackupUseCase.Result.CannotCreateBackupDirectory>(result)
			assertEquals(
				"Cannot create backup directory.",
				result.message,
			)

			verify {
				addLoggingUseCaseMock.invoke(
					"Error backup scripts file: Cannot create backup directory.",
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
				"test error",
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
				"Cannot create backup directory.",
				result.message,
			)

			verify {
				addLoggingUseCaseMock.invoke(
					"Error backup scripts file: Cannot create backup directory.",
				)
			}
		}

	@Test
	fun `should log error when backup file cannot created`() =
		runTest {
			var callCounter = 0
			val localScriptsRepository = ScriptsRepositoryMockHelper(
				getScriptFileLambda = {
					if (callCounter++ <= 1) {
						testFile.absolutePath
					} else {
						throw Exception("error: The source file doesn't exist.")
					}
				},
			)

			coEvery {
				getPreferenceUseCaseMock.get(
					STORE_KEY_FOR_BACKUP_STORAGE,
					any<Int>(),
				)
			} returns 3

			val result = createUseCase(scriptsRepository = localScriptsRepository).invoke()

			assertIs<RunFileBackupUseCase.Result.CannotCreateBackupFile>(result)
			assertEquals(
				"error: The source file doesn't exist.",
				result.message,
			)

			verify {
				addLoggingUseCaseMock.invoke(
					"Error backup scripts file: error: The source file doesn't exist.",
				)
			}
		}

	private fun write1MBFile(testFile: File) {
		val sizeInBytes = 1 * 1024 * 1024
		testFile.writeBytes(ByteArray(sizeInBytes))
	}

	private fun getBackupDirectory() =
		testFile.parentFile
			.listFiles()
			.firstOrNull { it.isDirectory && it.nameWithoutExtension == RunFileBackupUseCase.BACKUP_DIRECTORY }

	private fun getAllFilesFromBackupDirectory() =
		getBackupDirectory()
			?.walkTopDown()
			.orEmpty()
			.filter { it.isFile }
			.toList()

	private fun createBackupDirectory() {
		File(testFile.parentFile, RunFileBackupUseCase.BACKUP_DIRECTORY).mkdirs()
	}

	private fun createUseCase(scriptsRepository: ScriptsRepository = scriptsRepositoryMock) =
		RunFileBackupUseCase(
			scriptsRepository = scriptsRepository,
			getPreferenceUseCase = getPreferenceUseCaseMock,
			addLoggingUseCase = addLoggingUseCaseMock,
		)
}
