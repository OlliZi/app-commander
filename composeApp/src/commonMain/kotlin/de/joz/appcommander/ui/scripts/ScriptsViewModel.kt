package de.joz.appcommander.ui.scripts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.joz.appcommander.domain.ExecuteScriptUseCase
import de.joz.appcommander.domain.GetConnectedDevicesUseCase
import de.joz.appcommander.domain.GetScriptIdUseCase
import de.joz.appcommander.domain.GetUserScriptsUseCase
import de.joz.appcommander.domain.NavigationScreens
import de.joz.appcommander.domain.OpenScriptFileUseCase
import de.joz.appcommander.domain.ScriptsRepository
import de.joz.appcommander.domain.TrackScriptsFileChangesUseCase
import de.joz.appcommander.domain.logging.ClearLoggingUseCase
import de.joz.appcommander.domain.logging.GetLoggingUseCase
import de.joz.appcommander.ui.misc.UnidirectionalDataFlowViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class ScriptsViewModel(
	@InjectedParam private val navController: NavController,
	private val getConnectedDevicesUseCase: GetConnectedDevicesUseCase,
	private val getScriptIdUseCase: GetScriptIdUseCase,
	private val executeScriptUseCase: ExecuteScriptUseCase,
	private val getUserScriptsUseCase: GetUserScriptsUseCase,
	private val openScriptFileUseCase: OpenScriptFileUseCase,
	private val trackScriptsFileChangesUseCase: TrackScriptsFileChangesUseCase,
	private val clearLoggingUseCase: ClearLoggingUseCase,
	private val getLoggingUseCase: GetLoggingUseCase,
	private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
	private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel(),
	UnidirectionalDataFlowViewModel<ScriptsViewModel.UiState, ScriptsViewModel.Event> {
	private val _uiState = MutableStateFlow(UiState())
	override val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch(dispatcher) {
			onRefreshDevices()
			onRefreshScripts(getUserScriptsUseCase())

			trackScriptsFileChangesUseCase().collect { newEntries ->
				onRefreshScripts(newEntries)
			}
		}

		viewModelScope.launch(dispatcher) {
			getLoggingUseCase().collect { logging ->
				_uiState.update { oldState ->
					oldState.copy(
						logging =
							logging.mapIndexed { index, log ->
								"${index + 1}. $log"
							},
					)
				}
			}
		}
	}

	override fun onEvent(event: Event) {
		viewModelScope.launch(dispatcher) {
			when (event) {
				Event.OnNavigateToSettings -> navController.navigate(NavigationScreens.SettingsScreen)
				Event.OnRefreshDevices -> onRefreshDevices()
				Event.OnOpenScriptFile -> onOpenScriptFile()
				Event.OnNewScript -> onNewScript()
				Event.OnClearLogging -> onClearLogging()
				is Event.OnDeviceSelected -> onDeviceSelected(device = event.device)
				is Event.OnExecuteScript -> onExecuteScript(script = event.script)
				is Event.OnExecuteScriptText ->
					onExecuteScriptText(
						script = event.script,
						platform = event.platform,
					)

				is Event.OnExpandScript -> onExpandScript(script = event.script)
				is Event.OnEditScript -> onEditScript(script = event.script)
			}
		}
	}

	private suspend fun onRefreshDevices() {
		_uiState.update { oldState ->
			val devices = getConnectedDevicesUseCase()
			oldState.copy(
				connectedDevices =
					devices.mapIndexed { index, device ->
						Device(
							id = device.id,
							label = device.label,
							isSelected =
								devices.size == 1 ||
									oldState.connectedDevices.any { it.id == device.id && it.isSelected },
						)
					},
			)
		}
	}

	private fun onRefreshScripts(scripts: List<ScriptsRepository.Script>) {
		_uiState.update { oldState ->
			oldState.copy(
				scripts =
					scripts.map { script ->
						Script(
							description = script.label,
							scriptText = script.script,
							originalScript = script,
							isExpanded =
								_uiState.value.scripts.any {
									(it.description == script.label || it.scriptText == script.script) &&
										it.isExpanded
								},
						)
					},
			)
		}
	}

	private fun onDeviceSelected(device: Device) {
		_uiState.update { oldState ->
			oldState.copy(
				connectedDevices =
					oldState.connectedDevices.map {
						if (it.id == device.id) {
							it.copy(isSelected = it.isSelected.not())
						} else {
							it
						}
					},
			)
		}
	}

	private fun onExecuteScript(script: Script) {
		viewModelScope.launch(dispatcherIO) {
			_uiState.value.connectedDevices
				.filter {
					it.isSelected
				}.forEach { device ->
					executeScriptUseCase(script = script.originalScript, selectedDevice = device.id)
				}
		}
	}

	private fun onExecuteScriptText(
		script: String,
		platform: ScriptsRepository.Platform,
	) {
		viewModelScope.launch(dispatcherIO) {
			_uiState.value.connectedDevices
				.filter {
					it.isSelected
				}.forEach { device ->
					executeScriptUseCase(
						script =
							ScriptsRepository.Script(
								label = "entered by terminal script",
								script = script,
								platform = platform,
							),
						selectedDevice = device.id,
					)
				}
		}
	}

	private fun onExpandScript(script: Script) {
		_uiState.update { oldState ->
			oldState.copy(
				scripts =
					oldState.scripts.map { currentScript ->
						if (currentScript == script) {
							currentScript.copy(
								isExpanded = script.isExpanded.not(),
							)
						} else {
							currentScript
						}
					},
			)
		}
	}

	private fun onEditScript(script: Script) {
		navController.navigate(
			NavigationScreens.NewScriptScreen(
				scriptKey = getScriptIdUseCase(script.originalScript),
			),
		)
	}

	private fun onOpenScriptFile() {
		viewModelScope.launch(dispatcherIO) {
			openScriptFileUseCase()
		}
	}

	private fun onNewScript() {
		navController.navigate(
			NavigationScreens.NewScriptScreen(
				scriptKey = null,
			),
		)
	}

	private fun onClearLogging() {
		clearLoggingUseCase()
	}

	sealed interface Event {
		data object OnNavigateToSettings : Event

		data object OnRefreshDevices : Event

		data object OnOpenScriptFile : Event

		data object OnNewScript : Event

		data object OnClearLogging : Event

		data class OnDeviceSelected(
			val device: Device,
		) : Event

		data class OnExecuteScript(
			val script: Script,
		) : Event

		data class OnExecuteScriptText(
			val script: String,
			val platform: ScriptsRepository.Platform,
		) : Event

		data class OnExpandScript(
			val script: Script,
		) : Event

		data class OnEditScript(
			val script: Script,
		) : Event
	}

	data class UiState(
		val connectedDevices: List<Device> = emptyList(),
		val scripts: List<Script> = emptyList(),
		val logging: List<String> = emptyList(),
	)

	data class Device(
		val id: String,
		val label: String,
		val isSelected: Boolean,
	)

	data class Script(
		val description: String,
		val scriptText: String,
		val originalScript: ScriptsRepository.Script,
		val isExpanded: Boolean = false,
	)
}
