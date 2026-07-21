package de.joz.appcommander.domain.devices

import de.joz.appcommander.domain.model.Device
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetDevicesUseCaseImplTest {
	private val getSelectedDevicesUseCaseMock: GetSelectedDevicesUseCase = mockk()
	private val getConnectedDevicesUseCaseMock: GetConnectedDevicesUseCase = mockk()

	@Test
	fun `should return empty list when no devices are connected`() =
		runTest {
			coEvery {
				getSelectedDevicesUseCaseMock()
			} returns listOf(
				Device(id = "id 1", label = "label 1", isSelected = true),
			)
			coEvery {
				getConnectedDevicesUseCaseMock()
			} returns emptyList()

			val useCase = createUseCase()

			assertTrue(useCase.invoke().isEmpty())
		}

	@Test
	fun `should return empty list when get connected devices crashes`() =
		runTest {
			coEvery {
				getSelectedDevicesUseCaseMock()
			} returns listOf(
				Device(id = "id 1", label = "label 1", isSelected = true),
			)
			coEvery {
				getConnectedDevicesUseCaseMock()
			} throws Exception()

			val useCase = createUseCase()

			assertTrue(useCase.invoke().isEmpty())
		}

	@Test
	fun `should return empty list when get selected devices crashes`() =
		runTest {
			coEvery {
				getSelectedDevicesUseCaseMock()
			} throws Exception()
			coEvery {
				getConnectedDevicesUseCaseMock()
			} returns listOf(GetConnectedDevicesUseCase.ConnectedDevice(id = "id 1", label = "label 1"))

			val useCase = createUseCase()

			assertTrue(useCase.invoke().isEmpty())
		}

	@Test
	fun `should order devices by selection state and return`() =
		runTest {
			coEvery {
				getSelectedDevicesUseCaseMock()
			} returns listOf(
				Device(id = "unknown", label = "unknown", isSelected = false),
				Device(id = "id 1", label = "label 1", isSelected = false),
				Device(id = "id 3", label = "label 3", isSelected = false),
			)
			coEvery {
				getConnectedDevicesUseCaseMock()
			} returns listOf(
				GetConnectedDevicesUseCase.ConnectedDevice(id = "id 1", label = "label 1"),
				GetConnectedDevicesUseCase.ConnectedDevice(id = "id 3", label = "label 3"),
				GetConnectedDevicesUseCase.ConnectedDevice(id = "id 2", label = "label 2"),
			)

			val useCase = createUseCase()

			assertEquals(
				listOf(
					Device(id = "id 1", label = "label 1", isSelected = true),
					Device(id = "id 3", label = "label 3", isSelected = true),
					Device(id = "id 2", label = "label 2", isSelected = false),
				),
				useCase.invoke(),
			)
		}

	private fun createUseCase() =
		GetDevicesUseCaseImpl(
			getSelectedDevicesUseCase = getSelectedDevicesUseCaseMock,
			getConnectedDevicesUseCase = getConnectedDevicesUseCaseMock,
		)
}
