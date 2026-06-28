package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetSelectedDevicesUseCaseTest {
	private val selectedDevicesRepositoryMock: SelectedDevicesRepository = mockk(relaxed = true)
	private val getConnectedDevicesUseCaseMock: GetConnectedDevicesUseCase = mockk(relaxed = true)
	private val saveSelectedDevicesUseCaseMock: SaveSelectedDevicesUseCase = mockk(relaxed = true)
	private val selectedTestDevice = Device(id = "1", label = "label 1", isSelected = true)
	private val unSelectedTestDevice = Device(id = "2", label = "label 2", isSelected = false)

	@Test
	fun `should return no selected devices when no devices are connected`() =
		runTest {
			coEvery {
				getConnectedDevicesUseCaseMock()
			} returns emptyList()
			coEvery {
				selectedDevicesRepositoryMock.getSelectedDevices()
			} returns listOf(selectedTestDevice, unSelectedTestDevice)

			val result = createUseCase().invoke()

			assertTrue(result.isEmpty())
			coVerify {
				saveSelectedDevicesUseCaseMock.invoke(devices = emptyList())
			}
			coVerify {
				selectedDevicesRepositoryMock.getSelectedDevices()
			}
		}

	@Test
	fun `should return no selected devices when no devices are previously saved`() =
		runTest {
			coEvery {
				getConnectedDevicesUseCaseMock()
			} returns listOf(
				GetConnectedDevicesUseCase.ConnectedDevice(
					id = selectedTestDevice.id,
					label = selectedTestDevice.label,
				),
			)
			coEvery {
				selectedDevicesRepositoryMock.getSelectedDevices()
			} returns emptyList()

			val result = createUseCase().invoke()

			assertTrue(result.isEmpty())
			coVerify {
				saveSelectedDevicesUseCaseMock.invoke(devices = emptyList())
			}
			coVerify {
				selectedDevicesRepositoryMock.getSelectedDevices()
			}
		}

	@Test
	fun `should return no selected devices when no devices are previously selected`() =
		runTest {
			coEvery {
				getConnectedDevicesUseCaseMock()
			} returns listOf(
				GetConnectedDevicesUseCase.ConnectedDevice(
					id = unSelectedTestDevice.id,
					label = unSelectedTestDevice.label,
				),
			)
			coEvery {
				selectedDevicesRepositoryMock.getSelectedDevices()
			} returns listOf(unSelectedTestDevice)

			val result = createUseCase().invoke()

			assertTrue(result.isEmpty())
			coVerify {
				saveSelectedDevicesUseCaseMock.invoke(devices = emptyList())
			}
			coVerify {
				selectedDevicesRepositoryMock.getSelectedDevices()
			}
		}

	@Test
	fun `should return selected devices when devices are previously selected and they are some to the connected devices`() =
		runTest {
			coEvery {
				getConnectedDevicesUseCaseMock()
			} returns listOf(
				GetConnectedDevicesUseCase.ConnectedDevice(
					id = selectedTestDevice.id,
					label = selectedTestDevice.label,
				),
			)
			coEvery {
				selectedDevicesRepositoryMock.getSelectedDevices()
			} returns listOf(selectedTestDevice)

			val result = createUseCase().invoke()

			assertEquals(1, result.size)
			assertEquals(selectedTestDevice, result[0])
			coVerify {
				saveSelectedDevicesUseCaseMock.invoke(devices = result)
			}
			coVerify {
				selectedDevicesRepositoryMock.getSelectedDevices()
			}
		}

	private fun createUseCase() =
		GetSelectedDevicesUseCase(
			selectedDevicesRepository = selectedDevicesRepositoryMock,
			getConnectedDevicesUseCase = getConnectedDevicesUseCaseMock,
			saveSelectedDevicesUseCase = saveSelectedDevicesUseCaseMock,
		)
}
