package de.joz.appcommander.domain.script

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
					RunFileBackupUseCase.STORE_KEY_FOR_BACKUP_STORAGE,
					any<Int>(),
				)
			} returns -1
			createUseCase().invoke()

			assertEquals(contentBefore, testFile.readText())
			assertTrue(getBackupDirectory()?.listFiles().orEmpty().isEmpty())
		}

	@Test
	fun `should do a backup when strategy is MaximumStorage`() =
		runTest {
			val contentBefore = testFile.readText()

			coEvery {
				getPreferenceUseCaseMock.get(
					RunFileBackupUseCase.STORE_KEY_FOR_BACKUP_STORAGE,
					any<Int>(),
				)
			} returns 1

			createUseCase().invoke()

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
					RunFileBackupUseCase.STORE_KEY_FOR_BACKUP_STORAGE,
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

			useCase.invoke()

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
	fun `should log when an exception occurred`() =
		runTest {
			every { scriptsRepositoryMock.getScriptFile() } throws IllegalArgumentException("test error")

			createUseCase().invoke()

			verify { addLoggingUseCaseMock.invoke("Error backup the file: test error") }
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
