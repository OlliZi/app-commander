package de.joz.appcommander.domain

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import de.joz.appcommander.helper.IsJenkinsTestRunUseCase
import de.joz.appcommander.helper.IsLocalTestRunUseCase
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExecuteScriptUseCaseTest {
	private val addLoggingUseCaseMock: AddLoggingUseCase = mockk(relaxed = true)
	private val isLocalTestRunUseCase = IsLocalTestRunUseCase()
	private val isJenkinsTestRunUseCase = IsJenkinsTestRunUseCase()

	@Test
	fun `should execute script when launched`() =
		runTest {
			val executeScriptUseCase =
				ExecuteScriptUseCase(
					addLoggingUseCase = addLoggingUseCaseMock,
				)
			val script =
				ScriptsRepository.Script(
					label = "Test",
					script = "echo foo",
					platform = ScriptsRepository.Platform.ANDROID,
				)

			val result = executeScriptUseCase(script = script, selectedDevice = "Pixel7")

			assertTrue(result is ExecuteScriptUseCase.Result.Success)
			assertEquals("- foo\n", result.output)
			verify { addLoggingUseCaseMock.invoke("Execute script: 'echo foo' on device 'Pixel7'.") }
		}

	@Test
	fun `should chain multiples scripts when scripts are chained by special command &&`() =
		runTest {
			val executeScriptUseCase =
				ExecuteScriptUseCase(
					addLoggingUseCase = addLoggingUseCaseMock,
				)
			val script =
				ScriptsRepository.Script(
					label = "Test",
					script = "echo foo && echo bar && #LOOP_2 echo loop && echo test",
					platform = ScriptsRepository.Platform.ANDROID,
				)

			val result = executeScriptUseCase(script = script, selectedDevice = "Pixel7")

			assertTrue(result is ExecuteScriptUseCase.Result.Success)
			assertEquals("- foo\n- bar\n- loop\n- loop\n- test\n", result.output)
			verify {
				addLoggingUseCaseMock.invoke("Execute script: 'echo foo' on device 'Pixel7'.")
				addLoggingUseCaseMock.invoke("Execute script: 'echo bar' on device 'Pixel7'.")
				addLoggingUseCaseMock.invoke("Execute script: 'echo loop' on device 'Pixel7'.")
				addLoggingUseCaseMock.invoke("Execute script: 'echo loop' on device 'Pixel7'.")
				addLoggingUseCaseMock.invoke("Execute script: 'echo test' on device 'Pixel7'.")
			}
		}

	@Test
	fun `should execute script multiple times when special command '#LOOP_X' is prefixed`() =
		runTest {
			val executeScriptUseCase =
				ExecuteScriptUseCase(
					addLoggingUseCase = addLoggingUseCaseMock,
				)
			val script =
				ScriptsRepository.Script(
					label = "Test",
					script = "#LOOP_3 echo foo",
					platform = ScriptsRepository.Platform.ANDROID,
				)

			val result = executeScriptUseCase(script = script, selectedDevice = "Pixel7")

			assertTrue(result is ExecuteScriptUseCase.Result.Success)
			assertEquals("- foo\n- foo\n- foo\n", result.output)
			verify(exactly = 3) { addLoggingUseCaseMock.invoke("Execute script: 'echo foo' on device 'Pixel7'.") }
		}

	@Test
	fun `should return an failure if script execution fails`() =
		runTest {
			val executeScriptUseCase =
				ExecuteScriptUseCase(
					addLoggingUseCase = addLoggingUseCaseMock,
				)
			val script =
				ScriptsRepository.Script(
					label = "Test",
					script = "foo_bar_unknown_command",
					platform = ScriptsRepository.Platform.ANDROID,
				)

			val result = executeScriptUseCase(script = script, selectedDevice = "")

			assertTrue(result is ExecuteScriptUseCase.Result.Error)
			assertTrue(result.message.startsWith("Cannot run program \"foo_bar_unknown_command\" (in directory \".\"):"))
			verify { addLoggingUseCaseMock.invoke(any()) }
		}

	@Test
	fun `should append device id in script execution`() =
		runTest {
			if (!isLocalTestRunUseCase() && !isJenkinsTestRunUseCase()) {
				println("Cannot run test on github.")
				return@runTest
			}

			val executeScriptUseCase =
				ExecuteScriptUseCase(
					addLoggingUseCase = addLoggingUseCaseMock,
				)

			val script =
				ScriptsRepository.Script(
					label = "Test",
					script = "adb devices",
					platform = ScriptsRepository.Platform.ANDROID,
				)

			val result = executeScriptUseCase(script = script, selectedDevice = "Pixel7")

			assertTrue(result is ExecuteScriptUseCase.Result.Success)
			verify {
				addLoggingUseCaseMock.invoke(
					"Execute script: 'adb -s Pixel7 devices' on device 'Pixel7'.",
				)
			}
		}
}
