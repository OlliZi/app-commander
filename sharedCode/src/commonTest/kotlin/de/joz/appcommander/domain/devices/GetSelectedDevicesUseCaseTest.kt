package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
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

			val result = createUseCase().invoke().isEmpty()

			assertTrue(result)
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

			val result = createUseCase().invoke().isEmpty()

			assertTrue(result)
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

			val result = createUseCase().invoke().isEmpty()

			assertTrue(result)
			coVerify {
				saveSelectedDevicesUseCaseMock.invoke(devices = emptyList())
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
