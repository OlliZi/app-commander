package de.joz.appcommander.domain

import de.joz.appcommander.domain.logging.AddLoggingUseCase
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExecuteScriptUseCaseTest {

    private val addLoggingUseCaseMock: AddLoggingUseCase = mockk(relaxed = true)

    @Test
    fun `should execute script when launched`() = runTest {
        val executeScriptUseCase = ExecuteScriptUseCase(
            addLoggingUseCase = addLoggingUseCaseMock,
        )
        val script = ScriptsRepository.Script(
            label = "Test",
            script = "echo foo",
            platform = ScriptsRepository.Platform.ANDROID,
        )

        val result = executeScriptUseCase(script = script, selectedDevice = "Pixel7")

        assertTrue(result is ExecuteScriptUseCase.Result.Success)
        assertEquals("foo\n", result.output)
        verify { addLoggingUseCaseMock.invoke("Execute script: 'echo foo' on device 'Pixel7' ...") }
        verify { addLoggingUseCaseMock.invoke("Script executed: 'echo foo'.") }
    }

    @Test
    fun `should return an failure if script execution fails`() = runTest {
        val executeScriptUseCase = ExecuteScriptUseCase(
            addLoggingUseCase = addLoggingUseCaseMock,
        )
        val script = ScriptsRepository.Script(
            label = "Test",
            script = "foo_bar_unknown_command",
            platform = ScriptsRepository.Platform.ANDROID,
        )

        val result = executeScriptUseCase(script = script, selectedDevice = "")

        assertTrue(result is ExecuteScriptUseCase.Result.Error)
        assertEquals(
            "Cannot run program \"foo_bar_unknown_command\" (in directory \".\"): error=2, No such file or directory",
            result.message,
        )
        verify {
            addLoggingUseCaseMock.invoke("Cannot run program \"foo_bar_unknown_command\" (in directory \".\"): error=2, No such file or directory")
        }
    }
}