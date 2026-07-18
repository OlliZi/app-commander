package de.joz.appcommander.ui.misc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.joz.appcommander.MainDispatcher
import de.joz.appcommander.domain.devices.GetDevicesUseCase
import de.joz.appcommander.domain.devices.ObserveDevicesUseCase
import de.joz.appcommander.domain.devices.SaveSelectedDevicesUseCase
import de.joz.appcommander.domain.model.Device
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ConnectedDevicesViewModel(
	private val observeDevicesUseCase: ObserveDevicesUseCase,
	private val getDevicesUseCase: GetDevicesUseCase,
	private val saveSelectedDevicesUseCase: SaveSelectedDevicesUseCase,
	@MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) : ViewModel(),
	UnidirectionalDataFlowViewModel<ConnectedDevicesViewModel.UiState, ConnectedDevicesViewModel.Event> {
	private val _uiState = MutableStateFlow(UiState())
	override val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch(mainDispatcher) {
			observeDevicesUseCase().collect {
				onRefreshDevices(it)
			}
		}
	}

	override fun onEvent(event: Event) {
		viewModelScope.launch(mainDispatcher) {
			when (event) {
				is Event.OnDeviceSelect -> {
					onDeviceSelect(event.selectedDevice)
				}

				Event.OnRefreshDevices -> {
					onRefreshDevices(getDevicesUseCase())
				}
			}
		}
	}

	private fun onRefreshDevices(selectedDevices: List<Device>) {
		_uiState.update { oldState ->
			oldState.copy(
				connectedDevices = selectedDevices,
			)
		}
	}

	private suspend fun onDeviceSelect(selectedDevice: Device) {
		_uiState.update { oldState ->
			val updatedDevices = oldState.connectedDevices.map {
				if (it.id == selectedDevice.id) {
					it.copy(isSelected = it.isSelected.not())
				} else {
					it
				}
			}
			saveSelectedDevicesUseCase(devices = updatedDevices)
			oldState.copy(
				connectedDevices = updatedDevices,
			)
		}
	}

	sealed interface Event {
		data class OnDeviceSelect(
			val selectedDevice: Device,
		) : Event

		data object OnRefreshDevices : Event
	}

	data class UiState(
		val connectedDevices: List<Device> = emptyList(),
	)
}
