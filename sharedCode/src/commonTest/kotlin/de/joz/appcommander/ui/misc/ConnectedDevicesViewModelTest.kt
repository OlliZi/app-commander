package de.joz.appcommander.ui.misc

import de.joz.appcommander.domain.devices.GetDevicesUseCase
import de.joz.appcommander.domain.devices.ObserveDevicesUseCase
import de.joz.appcommander.domain.devices.SaveSelectedDevicesUseCase
import de.joz.appcommander.domain.model.Device
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectedDevicesViewModelTest {
	private val observeDevicesUseCaseMock: ObserveDevicesUseCase = mockk(relaxed = true)
	private val getDevicesUseCaseMock: GetDevicesUseCase = mockk(relaxed = true)
	private val saveSelectedDevicesUseCaseMock: SaveSelectedDevicesUseCase = mockk(relaxed = true)

	@Test
	fun `should load connected devices when viewmodel is initialized`() =
		runTest {
			coEvery { observeDevicesUseCaseMock() } returns flowOf(
				listOf(
					Device(id = "1", label = "P1", isSelected = true),
					Device(id = "2", label = "P2", isSelected = false),
				),
			)

			val viewModel = createViewModel()

			assertEquals(
				listOf(
					Device(id = "1", label = "P1", isSelected = true),
					Device(id = "2", label = "P2", isSelected = false),
				),
				viewModel.uiState.value.connectedDevices,
			)
		}

	@Test
	fun `should load connected devices when viewmodel is initialized and there is only one unselected device`() =
		runTest {
			coEvery { observeDevicesUseCaseMock() } returns flowOf(
				listOf(
					Device(id = "1", label = "P1", isSelected = false),
				),
			)

			val viewModel = createViewModel()

			assertEquals(
				listOf(
					Device(id = "1", label = "P1", isSelected = false),
				),
				viewModel.uiState.value.connectedDevices,
			)
		}

	@Test
	fun `should load connected devices when viewmodel is initialized and there is only one selected device`() =
		runTest {
			coEvery { observeDevicesUseCaseMock() } returns flowOf(
				listOf(
					Device(id = "1", label = "P1", isSelected = true),
				),
			)

			val viewModel = createViewModel()

			assertEquals(
				listOf(
					Device(id = "1", label = "P1", isSelected = true),
				),
				viewModel.uiState.value.connectedDevices,
			)
		}

	@Test
	fun `should refresh connected devices when event 'OnRefreshDevices' is triggered`() =
		runTest {
			coEvery { observeDevicesUseCaseMock() } returnsMany listOf(
				flowOf(
					listOf(
						Device(id = "1", label = "after init -> first load", isSelected = true),
					),
				),
			)

			coEvery {
				getDevicesUseCaseMock()
			} returns listOf(
				Device(id = "1", label = "after refresh -> second load", isSelected = false),
				Device(id = "2", label = "after refresh -> second load", isSelected = true),
				Device(id = "3", label = "after refresh -> second load", isSelected = false),
			)

			val viewModel = createViewModel()

			assertEquals(
				listOf(
					Device(id = "1", label = "after init -> first load", isSelected = true),
				),
				viewModel.uiState.value.connectedDevices,
			)

			viewModel.onEvent(event = ConnectedDevicesViewModel.Event.OnRefreshDevices)
			runCurrent()

			assertEquals(
				listOf(
					Device(id = "1", label = "after refresh -> second load", isSelected = false),
					Device(id = "2", label = "after refresh -> second load", isSelected = true),
					Device(id = "3", label = "after refresh -> second load", isSelected = false),
				),
				viewModel.uiState.value.connectedDevices,
			)
		}

	@Test
	fun `should select device when event 'OnDeviceSelected' is fired`() =
		runTest {
			coEvery { observeDevicesUseCaseMock() } returns flowOf(
				listOf(
					Device(id = "1", label = "P1", isSelected = true),
					Device(id = "2", label = "P2", isSelected = false),
				),
			)

			val viewModel = createViewModel()
			val device1 = viewModel.uiState.value.connectedDevices
				.first()
			val preSelectedState1 = device1.isSelected
			val device2 = viewModel.uiState.value.connectedDevices
				.last()
			val preSelectedState2 = device2.isSelected

			viewModel.onEvent(event = ConnectedDevicesViewModel.Event.OnDeviceSelect(selectedDevice = device1))
			runCurrent()

			val afterSelectedState1 = viewModel.uiState.value.connectedDevices
				.first()
				.isSelected
			val afterSelectedState2 = viewModel.uiState.value.connectedDevices
				.last()
				.isSelected

			assertTrue(preSelectedState1)
			assertFalse(preSelectedState2)

			assertFalse(afterSelectedState1)
			assertFalse(afterSelectedState2)

			coVerify {
				saveSelectedDevicesUseCaseMock.invoke(viewModel.uiState.value.connectedDevices)
			}
		}

	private fun createViewModel() =
		ConnectedDevicesViewModel(
			observeDevicesUseCase = observeDevicesUseCaseMock,
			getDevicesUseCase = getDevicesUseCaseMock,
			saveSelectedDevicesUseCase = saveSelectedDevicesUseCaseMock,
			mainDispatcher = Dispatchers.Unconfined,
		)
}
