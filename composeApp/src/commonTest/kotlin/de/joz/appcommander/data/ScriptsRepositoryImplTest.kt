package de.joz.appcommander.data

import de.joz.appcommander.domain.ScriptsRepository
import de.joz.appcommander.domain.logging.AddLoggingUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScriptsRepositoryImplTest {
	private val addLoggingUseCaseMock: AddLoggingUseCase = mockk(relaxed = true)
	private val testFile = File("./build", "test.json")

	@AfterTest
	fun setUp() {
		testFile.delete()
	}

	@Test
	fun `should return default scripts when file does not exist`() =
		runTest {
			val repository =
				ScriptsRepositoryImpl(
					scriptFile = testFile.absolutePath,
					addLoggingUseCase = addLoggingUseCaseMock,
				)

			assertFalse(testFile.exists())

			val scripts = repository.getScripts()

			assertTrue(testFile.exists())
			assertEquals(
				listOf(
					ScriptsRepository.Script(
						label = "Dark mode",
						script = "adb shell cmd uimode night yes",
						platform = ScriptsRepository.Platform.ANDROID,
					),
					ScriptsRepository.Script(
						label = "Light mode",
						script = "adb shell cmd uimode night no",
						platform = ScriptsRepository.Platform.ANDROID,
					),
				),
				scripts,
			)
		}

	@Test
	fun `should return custom scripts when file contains custom scripts`() =
		runTest {
			val prettyJson =
				Json {
					prettyPrint = true
				}
			testFile.writeText(
				text =
					prettyJson.encodeToString(
						listOf(
							ScriptsRepository.Script(
								label = "my script",
								script = "foo",
								platform = ScriptsRepository.Platform.ANDROID,
							),
							ScriptsRepository.Script(
								label = "my script abc",
								script = "bar",
								platform = ScriptsRepository.Platform.IOS,
							),
						),
					),
			)

			val repository =
				ScriptsRepositoryImpl(
					scriptFile = testFile.absolutePath,
					addLoggingUseCase = addLoggingUseCaseMock,
				)

			assertTrue(testFile.exists())

			val scripts = repository.getScripts()

			assertTrue(testFile.exists())
			assertEquals(
				listOf(
					ScriptsRepository.Script(
						label = "my script",
						script = "foo",
						platform = ScriptsRepository.Platform.ANDROID,
					),
					ScriptsRepository.Script(
						label = "my script abc",
						script = "bar",
						platform = ScriptsRepository.Platform.IOS,
					),
				),
				scripts,
			)
		}

	@Test
	fun `should return custom scripts when file contains custom scripts but with unknown fields`() =
		runTest {
			testFile.writeText(
				text =
					"[\n" +
						"    {\n" +
						"        \"unknown\": \"null\",\n" +
						"        \"label\": \"my script\",\n" +
						"        \"script\": \"foo\",\n" +
						"        \"platform\": \"ANDROID\"\n" +
						"    },\n" +
						"    {\n" +
						"        \"unknown\": \"\",\n" +
						"        \"label\": \"my script abc\",\n" +
						"        \"script\": \"bar\",\n" +
						"        \"platform\": \"IOS\"\n" +
						"    }\n" +
						"]",
			)

			val repository =
				ScriptsRepositoryImpl(
					scriptFile = testFile.absolutePath,
					addLoggingUseCase = addLoggingUseCaseMock,
				)

			assertTrue(testFile.exists())

			val scripts = repository.getScripts()

			assertTrue(testFile.exists())
			assertEquals(
				listOf(
					ScriptsRepository.Script(
						label = "my script",
						script = "foo",
						platform = ScriptsRepository.Platform.ANDROID,
					),
					ScriptsRepository.Script(
						label = "my script abc",
						script = "bar",
						platform = ScriptsRepository.Platform.IOS,
					),
				),
				scripts,
			)
		}

	@Test
	fun `should open script`() =
		runTest {
			val processBuilder: ProcessBuilder = mockk(relaxed = true)

			testFile.writeText("")

			ScriptsRepositoryImpl(
				scriptFile = testFile.absolutePath,
				processBuilder = processBuilder,
				addLoggingUseCase = addLoggingUseCaseMock,
			).openScriptFile()

			coVerify {
				processBuilder.command("open", testFile.absolutePath)
				processBuilder.start()
			}
		}

	@Test
	fun `should log error when opening script fails`() =
		runTest {
			val processBuilder: ProcessBuilder = mockk(relaxed = true)

			ScriptsRepositoryImpl(
				scriptFile = "unknown file",
				processBuilder = processBuilder,
				addLoggingUseCase = addLoggingUseCaseMock,
			).openScriptFile()

			coVerify {
				addLoggingUseCaseMock("Cannot open script file 'unknown file'. (Error: unknown file)")
			}
		}

	@Test
	fun `should save script when method is called`() =
		runTest {
			val repository =
				ScriptsRepositoryImpl(
					scriptFile = testFile.absolutePath,
					addLoggingUseCase = addLoggingUseCaseMock,
				)

			val newScript =
				ScriptsRepository.Script(
					script = "bar",
					label = "my script abc",
					platform = ScriptsRepository.Platform.IOS,
				)

			repository.saveScript(script = newScript)

			assertEquals(3, repository.getScripts().size)
			assertEquals(newScript, repository.getScripts().first())
		}

	@Test
	fun `should remove script when method is called`() =
		runTest {
			val repository =
				ScriptsRepositoryImpl(
					scriptFile = testFile.absolutePath,
					addLoggingUseCase = addLoggingUseCaseMock,
				)

			val scripts = repository.getScripts()
			val scriptToRemove = scripts.first()

			repository.removeScript(script = scriptToRemove)

			assertEquals(1, repository.getScripts().size)
			assertFalse(repository.getScripts().contains(scriptToRemove))
		}

	@Test
	fun `should update script when method is called`() =
		runTest {
			val repository =
				ScriptsRepositoryImpl(
					scriptFile = testFile.absolutePath,
					addLoggingUseCase = addLoggingUseCaseMock,
				)

			val scripts = repository.getScripts()
			val oldScript = scripts.first()

			val scriptToUpdate = oldScript.copy(label = "bar")
			repository.updateScript(script = scriptToUpdate, oldScript = oldScript)

			val updatedScripts = repository.getScripts()
			assertEquals(2, updatedScripts.size)
			assertFalse(updatedScripts.contains(oldScript))
			assertTrue(updatedScripts.contains(scriptToUpdate))
		}
}
