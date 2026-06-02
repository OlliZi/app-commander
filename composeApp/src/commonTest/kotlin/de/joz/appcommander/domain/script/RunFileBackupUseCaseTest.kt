package de.joz.appcommander.domain.script

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RunFileBackupUseCaseTest {
	private val testFile = File("./build", "test.json")
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

			createUseCase().invoke(backupStrategy = RunFileBackupUseCase.BackupStrategy.None)

			assertEquals(contentBefore, testFile.readText())
			assertTrue(getBackupDirectory()?.listFiles().orEmpty().isEmpty())
		}

	@Test
	fun `should do a backup when strategy is MaximumFiles`() =
		runTest {
			val contentBefore = testFile.readText()

			createUseCase().invoke(backupStrategy = RunFileBackupUseCase.BackupStrategy.MaximumFiles())

			assertEquals(contentBefore, testFile.readText())
			assertEquals(1, getBackupDirectory()?.listFiles().orEmpty().size)
			assertEquals(
				contentBefore,
				getBackupDirectory()?.listFiles()?.first()?.readText(),
			)
		}

	@Test
	fun `should do no backup when strategy is MaximumFiles but there are too many files`() =
		runTest {
			val backupStrategy = RunFileBackupUseCase.BackupStrategy.MaximumFiles()
			val contentBefore = testFile.readText()

			createBackupDirectory()

			val backupDirectory = getBackupDirectory()

			(1..backupStrategy.maxFiles).forEach {
				File(backupDirectory, "test_file_$it.json").writeText(it.toString())
			}

			assertEquals(contentBefore, testFile.readText())
			assertEquals(100, backupDirectory?.listFiles().orEmpty().size)

			createUseCase().invoke(backupStrategy = backupStrategy)

			assertEquals(contentBefore, testFile.readText())
			assertEquals(100, backupDirectory?.listFiles().orEmpty().size)
		}

	@Test
	fun `should do a backup when strategy is MaximumStorage`() =
		runTest {
			val contentBefore = testFile.readText()

			createUseCase().invoke(backupStrategy = RunFileBackupUseCase.BackupStrategy.MaximumStorage(maxMB = 1))

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
			val backupStrategy = RunFileBackupUseCase.BackupStrategy.MaximumStorage(maxMB = 3)
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

			useCase.invoke(backupStrategy = backupStrategy)

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

	private fun createUseCase() = RunFileBackupUseCase(scriptsRepository = scriptsRepositoryMock)
}
