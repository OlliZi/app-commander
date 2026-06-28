package de.joz.appcommander.data

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScriptsRepositoryImplTest {
	private val addLoggingUseCaseMock: AddLoggingUseCase = mockk(relaxed = true)
	private val testFile = File("./build", "test.json")

	@AfterTest
	fun tearDown() {
		testFile.delete()
	}

	@Test
	fun `should return same script file`() =
		runTest {
			val repository = createRepository()

			assertEquals(testFile.absolutePath, repository.getScriptFile())
		}

	@Test
	fun `should return default scripts when file does not exist`() =
		runTest {
			val repository = createRepository()

			assertFalse(testFile.exists())

			val scripts = repository.getScripts()

			assertTrue(testFile.exists())
			assertEquals(
				listOf(
					ScriptsRepository.Script(
						label = "Dark mode",
						scripts = listOf("adb shell cmd uimode night yes"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
					ScriptsRepository.Script(
						label = "Light mode",
						scripts = listOf("adb shell cmd uimode night no"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
					ScriptsRepository.Script(
						label = "Switch dark to light to dark mode",
						scripts = listOf(
							"adb shell cmd uimode night no",
							"sleep 1",
							"adb shell cmd uimode night yes",
							"sleep 1",
							"adb shell cmd uimode night no",
						),
						platform = ScriptsRepository.Platform.ANDROID,
					),
				),
				scripts.scripts,
			)
			assertNull(scripts.parsingMetaData)
		}

	@Test
	fun `should return default scripts and error when file contains invalid JSON`() =
		runTest {
			val repository = createRepository()

			testFile.writeText("{ key : invalid JSON, }")

			val scripts = repository.getScripts()

			assertEquals(
				listOf(
					ScriptsRepository.Script(
						label = "Dark mode",
						scripts = listOf("adb shell cmd uimode night yes"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
					ScriptsRepository.Script(
						label = "Light mode",
						scripts = listOf("adb shell cmd uimode night no"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
					ScriptsRepository.Script(
						label = "Switch dark to light to dark mode",
						scripts = listOf(
							"adb shell cmd uimode night no",
							"sleep 1",
							"adb shell cmd uimode night yes",
							"sleep 1",
							"adb shell cmd uimode night no",
						),
						platform = ScriptsRepository.Platform.ANDROID,
					),
				),
				scripts.scripts,
			)
			assertNotNull(scripts.parsingMetaData)
		}

	@Test
	fun `should return scripts and hint when scripts contains scripts trimmer`() =
		runTest {
			val repository = createRepository()

			testFile.writeText(
				"""
				[
					 {
						"label": "Light mode",
						"scripts": [
							"adb shell cmd uimode night no && sleep 1"
						],
						"platform": "ANDROID"
					}
				]
				""".trimIndent(),
			)

			val scripts = repository.getScripts()

			assertEquals(
				listOf(
					ScriptsRepository.Script(
						label = "Light mode",
						scripts = listOf("adb shell cmd uimode night no && sleep 1"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
				),
				scripts.scripts,
			)

			assertTrue(scripts.parsingMetaData is ScriptsRepository.ParsingMetaData.MultiScriptsHint)
		}

	@Test
	fun `should return scripts and hint when scripts contains old 'script' field`() =
		runTest {
			val repository = ScriptsRepositoryImpl(
				scriptFile = ScriptFile(scriptFile = testFile.absolutePath),
				addLoggingUseCase = addLoggingUseCaseMock,
				processBuilder = ProcessBuilder(),
			)

			testFile.writeText(
				"""
				[
					 {
						"label": "Light mode",
						"script": "ERROR",
						"scripts": [
							 "adb shell cmd uimode night no"
						],
						"platform": "ANDROID"
					}
				]
				""".trimIndent(),
			)

			val scripts = repository.getScripts()

			assertEquals(
				listOf(
					ScriptsRepository.Script(
						label = "Light mode",
						scripts = listOf("adb shell cmd uimode night no"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
				),
				scripts.scripts,
			)

			assertTrue(scripts.parsingMetaData is ScriptsRepository.ParsingMetaData.OldScriptFieldHint)
		}

	@Test
	fun `should return custom scripts when file contains custom scripts`() =
		runTest {
			val jsonHandler = Json {
				prettyPrint = true
			}
			testFile.writeText(
				text = jsonHandler.encodeToString(
					listOf(
						ScriptsRepository.Script(
							label = "my script",
							scripts = listOf("foo"),
							platform = ScriptsRepository.Platform.ANDROID,
						),
						ScriptsRepository.Script(
							label = "my script abc",
							scripts = listOf("bar"),
							platform = ScriptsRepository.Platform.IOS,
						),
					),
				),
			)

			val repository = createRepository()

			assertTrue(testFile.exists())

			val scripts = repository.getScripts()

			assertTrue(testFile.exists())
			assertEquals(
				listOf(
					ScriptsRepository.Script(
						label = "my script",
						scripts = listOf("foo"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
					ScriptsRepository.Script(
						label = "my script abc",
						scripts = listOf("bar"),
						platform = ScriptsRepository.Platform.IOS,
					),
				),
				scripts.scripts,
			)
			assertNull(scripts.parsingMetaData)
		}

	@Test
	fun `should return custom scripts when file contains custom scripts but with unknown fields`() =
		runTest {
			testFile.writeText(
				text =
					"[\n" + "    {\n" + "        \"unknown\": \"null\",\n" + "        \"label\": \"my script\",\n" +
						"        \"scripts\": [\"foo\"],\n" +
						"        \"platform\": \"ANDROID\"\n" +
						"    },\n" +
						"    {\n" +
						"        \"unknown\": \"\",\n" +
						"        \"label\": \"my script abc\",\n" +
						"        \"scripts\": [\"bar\"],\n" +
						"        \"platform\": \"IOS\"\n" +
						"    }\n" +
						"]",
			)

			val repository = createRepository()

			assertTrue(testFile.exists())

			val scripts = repository.getScripts()

			assertTrue(testFile.exists())
			assertEquals(
				listOf(
					ScriptsRepository.Script(
						label = "my script",
						scripts = listOf("foo"),
						platform = ScriptsRepository.Platform.ANDROID,
					),
					ScriptsRepository.Script(
						label = "my script abc",
						scripts = listOf("bar"),
						platform = ScriptsRepository.Platform.IOS,
					),
				),
				scripts.scripts,
			)
			assertNull(scripts.parsingMetaData)
		}

	@Test
	fun `should open script`() =
		runTest {
			val processBuilder: ProcessBuilder = mockk(relaxed = true)

			testFile.writeText("")

			ScriptsRepositoryImpl(
				scriptFile = ScriptFile(scriptFile = testFile.absolutePath),
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
				scriptFile = ScriptFile(scriptFile = "unknown file"),
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
			val repository = createRepository()

			val newScript = ScriptsRepository.Script(
				scripts = listOf("bar"),
				label = "my script abc",
				platform = ScriptsRepository.Platform.IOS,
			)

			val result = repository.saveScript(script = newScript)

			assertIs<ScriptsRepository.WriteScriptResult.Success>(result)

			assertEquals(4, repository.getScripts().scripts.size)
			assertEquals(newScript, repository.getScripts().scripts.first())
		}

	@Test
	fun `should remove script when method is called`() =
		runTest {
			val repository = createRepository()

			val scripts = repository.getScripts().scripts
			val scriptToRemove = scripts.first()

			val result = repository.removeScript(script = scriptToRemove)

			assertIs<ScriptsRepository.WriteScriptResult.Success>(result)
			assertEquals(2, repository.getScripts().scripts.size)
			assertFalse(repository.getScripts().scripts.contains(scriptToRemove))
		}

	@Test
	fun `should update script when method is called`() =
		runTest {
			val repository = createRepository()

			val scripts = repository.getScripts()
			val oldScript = scripts.scripts.first()
			val scriptToUpdate = oldScript.copy(label = "bar")

			val result = repository.updateScript(script = scriptToUpdate, oldScript = oldScript)

			assertIs<ScriptsRepository.WriteScriptResult.Success>(result)

			val updatedScripts = repository.getScripts()
			assertEquals(3, updatedScripts.scripts.size)
			assertFalse(updatedScripts.scripts.contains(oldScript))
			assertTrue(updatedScripts.scripts.contains(scriptToUpdate))
		}

	@Test
	fun `should return an error when updating fails`() =
		runTest {
			val repository = createRepository()

			val result = repository.updateScript(script = mockk(), oldScript = mockk())

			assertIs<ScriptsRepository.WriteScriptResult.UpdateError>(result)
			assertFalse(result.message.isBlank())
		}

	@Test
	fun `should return an error when saving fails`() =
		runTest {
			val repository = createRepository()

			val result = repository.saveScript(script = mockk())

			assertIs<ScriptsRepository.WriteScriptResult.SaveError>(result)
			assertFalse(result.message.isBlank())
		}

	@Test
	fun `should return an error when removing fails`() =
		runTest {
			val repository = createRepository(
				scriptFile = "",
			)

			val result = repository.removeScript(script = mockk())

			assertIs<ScriptsRepository.WriteScriptResult.RemoveError>(result)
			assertFalse(result.message.isBlank())
		}

	private fun createRepository(scriptFile: String = testFile.absolutePath) =
		ScriptsRepositoryImpl(
			scriptFile = ScriptFile(scriptFile = scriptFile),
			addLoggingUseCase = addLoggingUseCaseMock,
			processBuilder = ProcessBuilder(),
		)
}
