package de.joz.appcommander.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetConnectedDevicesUseCaseTest {

    @Test
    fun `should return connected android devices when use case is executed`() = runTest {
        val executeScriptUseCase: ExecuteScriptUseCase = mockk()
        coEvery {
            executeScriptUseCase(
                any(),
                any()
            )
        } returns ScriptRunner.Result.Success("List of devices attached\n\npixel-7")

        val getConnectedDevicesUseCase = GetConnectedDevicesUseCase(executeScriptUseCase)
        val result = getConnectedDevicesUseCase()

        assertEquals(listOf("pixel-7"), result)
        coVerify { executeScriptUseCase(script = "adb devices", selectedDevice = "") }
    }

    @Test
    fun `should return nothing when no android devices are connected`() = runTest {
        val executeScriptUseCase: ExecuteScriptUseCase = mockk()
        coEvery {
            executeScriptUseCase(
                any(),
                any()
            )
        } returns ScriptRunner.Result.Success("List of devices attached\n\n")

        val getConnectedDevicesUseCase = GetConnectedDevicesUseCase(executeScriptUseCase)
        val result = getConnectedDevicesUseCase()

        assertEquals(emptyList(), result)
        coVerify { executeScriptUseCase(script = "adb devices", selectedDevice = "") }
    }

    @Test
    fun `should return nothing when an error occurred`() = runTest {
        val executeScriptUseCase: ExecuteScriptUseCase = mockk()
        coEvery {
            executeScriptUseCase(
                any(),
                any()
            )
        } returns ScriptRunner.Result.Error(message = "mock error")

        val getConnectedDevicesUseCase = GetConnectedDevicesUseCase(executeScriptUseCase)
        val result = getConnectedDevicesUseCase()

        assertEquals(emptyList(), result)
        coVerify { executeScriptUseCase(script = "adb devices", selectedDevice = "") }
    }
}