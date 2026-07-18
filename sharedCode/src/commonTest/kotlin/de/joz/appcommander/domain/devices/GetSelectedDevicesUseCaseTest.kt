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
	private val selectedTestDevice = Device(id = "1", label = "label 1", isSelected = true)
	private val unSelectedTestDevice = Device(id = "2", label = "label 2", isSelected = false)

	@Test
	fun `should return selected devices when repository contains devices`() =
		runTest {
			coEvery {
				selectedDevicesRepositoryMock.getSelectedDevices()
			} returns listOf(selectedTestDevice, unSelectedTestDevice)

			val result = createUseCase().invoke()

			assertTrue(result.isNotEmpty())
			coVerify {
				selectedDevicesRepositoryMock.getSelectedDevices()
			}
		}

	private fun createUseCase() =
		GetSelectedDevicesUseCase(
			selectedDevicesRepository = selectedDevicesRepositoryMock,
		)
}
