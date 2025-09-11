package de.joz.appcommander.ui.scripts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.joz.appcommander.domain.GetConnectedDevivesUseCase
import de.joz.appcommander.domain.NavigationScreens
import de.joz.appcommander.ui.misc.UnidirectionalDataFlowViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class ScriptsViewModel(
    @InjectedParam
    private val navController: NavController,
    private val getConnectedDevivesUseCase: GetConnectedDevivesUseCase,
) : ViewModel(),
    UnidirectionalDataFlowViewModel<ScriptsViewModel.UiState, ScriptsViewModel.Event> {

    private val _uiState = MutableStateFlow(UiState())
    override val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { oldState ->
                val devices = getConnectedDevivesUseCase()
                oldState.copy(
                    connectedDevices = devices.mapIndexed { index, device ->
                        Device(
                            id = index,
                            label = device,
                            isSelected = devices.size == 1,
                        )
                    },
                )
            }
        }
    }

    override fun onEvent(event: Event) {
        viewModelScope.launch {
            when (event) {
                is Event.OnDeviceSelected -> onDeviceSelected(device = event.device)
                Event.OnNavigateToSettings -> navController.navigate(NavigationScreens.SettingsScreen)
            }
        }
    }

    private fun onDeviceSelected(device: Device) {
        _uiState.update { oldState ->
            oldState.copy(
                connectedDevices = oldState.connectedDevices.map {
                    if (it.id == device.id) {
                        it.copy(isSelected = it.isSelected.not())
                    } else {
                        it
                    }
                }
            )
        }
    }

    sealed interface Event {
        data class OnDeviceSelected(val device: Device) : Event
        data object OnNavigateToSettings : Event
    }

    data class UiState(
        val connectedDevices: List<Device> = emptyList(),
    )

    data class Device(
        val id: Int,
        val label: String,
        val isSelected: Boolean,
    )
}