package de.joz.app_commander.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ExecuteScriptUseCaseTest {

    @Test
    fun `should execute script runner when use case is executed`() = runTest {
        val scriptRunner: ScriptRunner = mockk()
        coEvery {
            scriptRunner.executeScript(
                any(),
                any()
            )
        } returns ScriptRunner.Result.Success("output")

        val executeScriptUseCase = ExecuteScriptUseCase(scriptRunner)
        executeScriptUseCase(script = "script", selectedDevice = "pixel 9")

        coVerify { scriptRunner.executeScript(script = "script", selectedDevice = "pixel 9") }
    }
}