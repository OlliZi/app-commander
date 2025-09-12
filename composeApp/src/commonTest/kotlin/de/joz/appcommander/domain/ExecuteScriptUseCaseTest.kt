package de.joz.appcommander.domain

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExecuteScriptUseCaseTest {

    @Test
    fun `should execute script when launched`() = runTest {
        val executeScriptUseCase = ExecuteScriptUseCase()

        val result = executeScriptUseCase("echo foo", "Pixel7")

        assertTrue(result is ExecuteScriptUseCase.Result.Success)
        assertEquals("foo\n", result.output)
    }

    @Test
    fun `should return an failure if script execution fails`() = runTest {
        val executeScriptUseCase = ExecuteScriptUseCase()

        val result = executeScriptUseCase("foo_bar_unknown_command", "")

        assertTrue(result is ExecuteScriptUseCase.Result.Error)
        assertEquals(
            "Cannot run program \"foo_bar_unknown_command\" (in directory \".\"): error=2, No such file or directory",
            result.message
        )
    }
}