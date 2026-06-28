package de.joz.appcommander.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.joz.appcommander.IODispatcher
import de.joz.appcommander.MainDispatcher
import de.joz.appcommander.domain.devices.GetConnectedDevicesUseCase
import de.joz.appcommander.domain.model.Device
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptByKeyUseCase
import de.joz.appcommander.domain.script.RemoveUserScriptUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.ui.misc.TypedStringResource
import de.joz.appcommander.ui.misc.UnidirectionalDataFlowViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class EditScriptViewModel(
	@InjectedParam private val navController: NavController,
	@InjectedParam private var scriptKey: Int?,
	getUserScriptByKeyUseCase: GetUserScriptByKeyUseCase,
	private val getConnectedDevicesUseCase: GetConnectedDevicesUseCase,
	private val getScriptIdUseCase: GetScriptIdUseCase,
	private val executeScriptUseCase: ExecuteScriptUseCase,
	private val saveUserScriptUseCase: SaveUserScriptUseCase,
	private val removeUserScriptUseCase: RemoveUserScriptUseCase,
	private val saveUserScriptUseCaseResultMapper: SaveUserScriptUseCaseResultMapper,
	@MainDispatcher private val mainDispatcher: CoroutineDispatcher,
	@IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel(),
	UnidirectionalDataFlowViewModel<EditScriptViewModel.UiState, EditScriptViewModel.Event> {
	private val _uiState = MutableStateFlow(
		initToUiState(getUserScriptByKeyUseCase(scriptKey)),
	)
	private val originalUiState = _uiState.value

	init {
		viewModelScope.launch(mainDispatcher) {
			onRefreshDevices()
		}
	}

	override val uiState = _uiState.asStateFlow()

	override fun onEvent(event: Event) {
		viewModelScope.launch(mainDispatcher) {
			when (event) {
				is Event.OnNavigateBack -> onNavigateBack()
				is Event.OnSelectPlatform -> onSelectPlatform(event.platform)
				is Event.OnChangeScript -> onChangeScript(event.index, event.script)
				is Event.OnAddSubScript -> onAddSubScript(event.index)
				is Event.OnRemoveSubScript -> onRemoveSubScript(event.index)
				is Event.OnChangeScriptName -> onChangeScriptName(event.scriptName)
				is Event.OnExecuteSingleScript -> onExecuteSingleScript(event.script)
				is Event.OnExecuteAllScripts -> onExecuteAllScripts()
				is Event.OnSaveScript -> onSaveScript()
				is Event.OnRemoveScript -> onRemoveScript()
				is Event.OnDeviceSelected -> onDeviceSelected(device = event.device)
				Event.OnRefreshDevices -> onRefreshDevices()
			}
		}
	}

	private fun onNavigateBack() {
		navController.navigateUp()
	}

	private fun onSelectPlatform(platform: ScriptsRepository.Platform) {
		updateUiState(selectedPlatform = platform)
	}

	private fun onChangeScript(
		index: Int,
		script: String,
	) {
		updateUiState(
			scripts = _uiState.value.scriptUiState.scripts.mapIndexed { oldIndex, oldScript ->
				if (oldIndex == index) {
					script
				} else {
					oldScript
				}
			},
		)
	}

	private fun onAddSubScript(index: Int) {
		updateUiState(
			scripts = _uiState.value.scriptUiState.scripts
				.toMutableList()
				.apply {
					add(index + 1, "<enter new script>")
				}.toList(),
		)
	}

	private fun onRemoveSubScript(index: Int) {
		updateUiState(
			scripts = if (_uiState.value.scriptUiState.scripts.size == 1) {
				listOf("")
			} else {
				_uiState.value.scriptUiState.scripts.filterIndexed { oldIndex, _ ->
					oldIndex != index
				}
			},
		)
	}

	private fun onChangeScriptName(scriptName: String) {
		updateUiState(scriptName = scriptName)
	}

	private fun updateUiState(
		scriptName: String? = null,
		scripts: List<String>? = null,
		selectedPlatform: ScriptsRepository.Platform? = null,
	) {
		_uiState.update { oldState ->
			val newScript = oldState.scriptUiState.copy(
				scriptName = scriptName ?: oldState.scriptUiState.scriptName,
				scripts = scripts ?: oldState.scriptUiState.scripts,
				selectedPlatform = selectedPlatform ?: oldState.scriptUiState.selectedPlatform,
			)
			oldState.copy(
				scriptChanged = newScript != originalUiState.scriptUiState,
				scriptUiState = newScript,
				showDeviceSelection = newScript.selectedPlatform.canShowDeviceSelection(),
			)
		}
	}

	private fun onExecuteSingleScript(script: String) {
		val platform = _uiState.value.scriptUiState.selectedPlatform
		if (platform == ScriptsRepository.Platform.DESKTOP) {
			executeScriptHelper(
				script = script,
				platform = platform,
			)
		} else {
			_uiState.value.connectedDevices
				.filter {
					it.isSelected
				}.forEach { device ->
					executeScriptHelper(
						script = script,
						platform = platform,
						device = device.id,
					)
				}
		}
	}

	private fun onExecuteAllScripts() {
		val platform = _uiState.value.scriptUiState.selectedPlatform
		if (platform == ScriptsRepository.Platform.DESKTOP) {
			viewModelScope.launch(ioDispatcher) {
				executeScriptUseCase(
					script = _uiState.value.scriptUiState.toScriptsRepositoryScript(),
					selectedDevice = "",
				)
			}
		} else {
			_uiState.value.connectedDevices
				.filter {
					it.isSelected
				}.forEach { device ->
					viewModelScope.launch(ioDispatcher) {
						executeScriptUseCase(
							script = _uiState.value.scriptUiState.toScriptsRepositoryScript(),
							selectedDevice = device.id,
						)
					}
				}
		}
	}

	private fun executeScriptHelper(
		script: String,
		platform: ScriptsRepository.Platform,
		device: String = "",
	) {
		viewModelScope.launch(ioDispatcher) {
			executeScriptUseCase(
				script = ScriptsRepository.Script(
					label = "",
					scripts = listOf(script),
					platform = platform,
				),
				selectedDevice = device,
			)
		}
	}

	private fun onSaveScript() {
		viewModelScope.launch(ioDispatcher) {
			val scriptToSave = _uiState.value.scriptUiState.toScriptsRepositoryScript()
			val result = saveUserScriptUseCase(
				script = scriptToSave,
				scriptKey = scriptKey,
			)

			val errorMessages = saveUserScriptUseCaseResultMapper(result = result)

			scriptKey = getScriptIdUseCase(scriptToSave)

			_uiState.update { oldState ->
				oldState.copy(errorMessages = errorMessages)
			}

			if (errorMessages.isEmpty()) {
				viewModelScope.launch(mainDispatcher) {
					onNavigateBack()
				}
			}
		}
	}

	private fun onRemoveScript() {
		viewModelScope.launch(ioDispatcher) {
			removeUserScriptUseCase(
				script = originalUiState.scriptUiState.toScriptsRepositoryScript(),
			)
		}

		onNavigateBack()
	}

	private suspend fun onRefreshDevices() {
		_uiState.update { oldState ->
			val devices = getConnectedDevicesUseCase()
			oldState.copy(
				connectedDevices = devices.map { device ->
					Device(
						id = device.id,
						label = device.label,
						isSelected = devices.size == 1 || oldState.connectedDevices.any { it.id == device.id && it.isSelected },
					)
				},
			)
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
				},
			)
		}
	}

	private fun initToUiState(script: ScriptsRepository.Script?): UiState =
		UiState(
			scriptChanged = false,
			showDeviceSelection = script?.platform.canShowDeviceSelection(),
			scriptUiState = ScriptUiState(
				scriptName = script?.label.orEmpty(),
				scripts = script?.scripts ?: listOf(""),
				selectedPlatform = script?.platform ?: ScriptsRepository.Platform.ANDROID,
			),
		)

	private fun ScriptsRepository.Platform?.canShowDeviceSelection() = this != ScriptsRepository.Platform.DESKTOP

	sealed interface Event {
		data object OnNavigateBack : Event

		data object OnSaveScript : Event

		data object OnRemoveScript : Event

		data object OnRefreshDevices : Event

		data class OnDeviceSelected(
			val device: Device,
		) : Event

		data class OnExecuteSingleScript(
			val script: String,
		) : Event

		data object OnExecuteAllScripts : Event

		data class OnChangeScript(
			val index: Int,
			val script: String,
		) : Event

		data class OnAddSubScript(
			val index: Int,
		) : Event

		data class OnRemoveSubScript(
			val index: Int,
		) : Event

		data class OnChangeScriptName(
			val scriptName: String,
		) : Event

		data class OnSelectPlatform(
			val platform: ScriptsRepository.Platform,
		) : Event
	}

	data class UiState(
		val scriptChanged: Boolean = false,
		val showDeviceSelection: Boolean = true,
		val scriptUiState: ScriptUiState = ScriptUiState(),
		val errorMessages: List<TypedStringResource> = emptyList(),
		val connectedDevices: List<Device> = emptyList(),
	)

	data class ScriptUiState(
		val scripts: List<String> = emptyList(),
		val scriptName: String = "",
		val selectedPlatform: ScriptsRepository.Platform = ScriptsRepository.Platform.ANDROID,
	)

	private fun ScriptUiState.toScriptsRepositoryScript() =
		ScriptsRepository.Script(
			label = scriptName,
			scripts = scripts,
			platform = selectedPlatform,
		)
}
