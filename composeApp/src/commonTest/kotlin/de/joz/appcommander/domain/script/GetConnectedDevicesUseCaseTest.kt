package de.joz.appcommander.domain.script

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetConnectedDevicesUseCaseTest {
	@Test
	fun `should return connected android devices when use case is executed`() =
		runTest {
			val executeScriptUseCase: ExecuteScriptUseCase = mockk()
			coEvery {
				executeScriptUseCase(
					any(),
					any(),
				)
			} returns
				ExecuteScriptUseCase.Result.Success(
					"List of devices attached\n\ndevice-7\tdevice",
				)

			val getConnectedDevicesUseCase = GetConnectedDevicesUseCase(executeScriptUseCase)
			val result = getConnectedDevicesUseCase()

			assertEquals(
				listOf(
					// Android
					GetConnectedDevicesUseCase.ConnectedDevice(
						id = "device-7",
						label = "device-7\tdevice",
					),
					// iOS
					GetConnectedDevicesUseCase.ConnectedDevice(
						id = "device-7\tdevice",
						label = "device-7\tdevice",
					),
				),
				result,
			)
			coVerify {
				executeScriptUseCase(
					script = GetConnectedDevicesUseCase.ANDROID_GET_DEVICES_SCRIPT,
					selectedDevice = "",
				)
			}
		}

	@Test
	fun `should return nothing when no android devices are connected`() =
		runTest {
			val executeScriptUseCase: ExecuteScriptUseCase = mockk()
			coEvery {
				executeScriptUseCase(
					any(),
					any(),
				)
			} returns ExecuteScriptUseCase.Result.Success("List of devices attached\n\n")

			val getConnectedDevicesUseCase = GetConnectedDevicesUseCase(executeScriptUseCase)
			val result = getConnectedDevicesUseCase()

			assertEquals(emptyList(), result)
			coVerify {
				executeScriptUseCase(
					script = GetConnectedDevicesUseCase.ANDROID_GET_DEVICES_SCRIPT,
					selectedDevice = "",
				)
			}
		}

	@Test
	fun `should return nothing when an error occurred`() =
		runTest {
			val executeScriptUseCase: ExecuteScriptUseCase = mockk()
			coEvery {
				executeScriptUseCase(
					any(),
					any(),
				)
			} returns ExecuteScriptUseCase.Result.Error(message = "mock error")

			val getConnectedDevicesUseCase = GetConnectedDevicesUseCase(executeScriptUseCase)
			val result = getConnectedDevicesUseCase()

			assertEquals(emptyList(), result)
			coVerify {
				executeScriptUseCase(
					script = GetConnectedDevicesUseCase.ANDROID_GET_DEVICES_SCRIPT,
					selectedDevice = "",
				)
			}
		}
}
