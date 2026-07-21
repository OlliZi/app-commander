package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SaveSelectedDevicesUseCaseTest {
	@Test
	fun `should save selected devices when use case is executed`() =
		runTest {
			val selectedDevicesRepositoryMock: SelectedDevicesRepository = mockk(relaxed = true)

			val useCase = SaveSelectedDevicesUseCase(
				selectedDevicesRepository = selectedDevicesRepositoryMock,
			)

			useCase.invoke(
				devices = listOf(
					Device(id = "id 1", label = "label 1", isSelected = true),
					Device(id = "id 2", label = "label 2", isSelected = false),
				),
			)

			coVerify {
				selectedDevicesRepositoryMock.saveSelectedDevices(
					devices = listOf(
						Device(id = "id 1", label = "label 1", isSelected = true),
					),
				)
			}
		}
}
