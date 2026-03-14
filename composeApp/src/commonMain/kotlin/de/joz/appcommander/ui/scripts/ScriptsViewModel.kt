package de.joz.appcommander.ui.scripts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.joz.appcommander.IODispatcher
import de.joz.appcommander.MainDispatcher
import de.joz.appcommander.domain.logging.ClearLoggingUseCase
import de.joz.appcommander.domain.logging.GetLoggingUseCase
import de.joz.appcommander.domain.navigation.NavigationScreens
import de.joz.appcommander.domain.preference.ChangedPreference
import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.domain.preference.SavePreferenceUseCase
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetConnectedDevicesUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptsUseCase
import de.joz.appcommander.domain.script.OpenScriptFileUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.domain.script.TrackScriptsFileChangesUseCase
import de.joz.appcommander.ui.misc.UnidirectionalDataFlowViewModel
import de.joz.appcommander.ui.model.Hint
import de.joz.appcommander.ui.model.ToolSection
import kotlinx.coroutines.CoroutineDispatcher
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
	private val getPreferenceUseCase: GetPreferenceUseCase,
	private val savePreferenceUseCase: SavePreferenceUseCase,
	@MainDispatcher private val mainDispatcher: CoroutineDispatcher,
	@IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel(),
	UnidirectionalDataFlowViewModel<ScriptsViewModel.UiState, ScriptsViewModel.Event> {
	private val _uiState = MutableStateFlow(UiState())
	override val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch(mainDispatcher) {
			onRefreshDevices()
			onRefreshScripts(getUserScriptsUseCase())

			trackScriptsFileChangesUseCase().collect { newEntries ->
				onRefreshScripts(newEntries)
			}
		}

		viewModelScope.launch(mainDispatcher) {
			getLoggingUseCase().collect { logging ->
				_uiState.update { oldState ->
					oldState.copy(
						logging = logging.mapIndexed { index, log ->
							"${index + 1}. $log"
						},
					)
				}
			}
		}

		viewModelScope.launch(mainDispatcher) {
			val keys = ToolSection.entries.map { it.name }.toTypedArray()
			getPreferenceUseCase.getAsFlow(keys = keys).collect { changedValues ->
				onRefreshToolSections(changedValues)
			}
		}
	}

	override fun onEvent(event: Event) {
		viewModelScope.launch(mainDispatcher) {
			when (event) {
				Event.OnNavigateToSettings -> {
					navController.navigate(NavigationScreens.SettingsScreen)
				}

				Event.OnRefreshDevices -> {
					onRefreshDevices()
				}

				Event.OnOpenScriptFile -> {
					onOpenScriptFile()
				}

				Event.OnNewScript -> {
					onNewScript()
				}

				Event.OnClearLogging -> {
					onClearLogging()
				}

				is Event.OnDeviceSelected -> {
					onDeviceSelected(device = event.device)
				}

				is Event.OnExecuteScript -> {
					onExecuteScript(script = event.script)
				}

				is Event.OnExecuteScriptText -> {
					onExecuteScriptText(
						script = event.script,
						platform = event.platform,
					)
				}

				is Event.OnExpandScript -> {
					onExpandScript(script = event.script)
				}

				is Event.OnEditScript -> {
					onEditScript(script = event.script)
				}

				is Event.OnFilterScripts -> {
					onFilterScripts(filter = event.filter)
				}
			}
		}
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

	private fun onRefreshToolSections(changedValues: List<ChangedPreference>) {
		_uiState.update { oldState ->
			oldState.copy(
				toolSections = ToolSection.entries.filter { toolSection ->
					changedValues
						.firstOrNull {
							it.key == toolSection.name
						}?.value as? Boolean ?: toolSection.isDefaultActive
				},
			)
		}
	}

	private suspend fun onRefreshScripts(jsonParseResult: ScriptsRepository.JsonParseResult) {
		val filter = getPreferenceUseCase.get(SCRIPT_FILTER_PREF_KEY, "").lowercase()

		_uiState.update { oldState ->
			oldState.copy(
				hint = mapHint(jsonParseResult.parsingMetaData),
				filter = filter,
				scripts = jsonParseResult.scripts
					.filter {
						it.label.lowercase().contains(filter) ||
							it.scripts.any { script ->
								script.lowercase().contains(filter)
							} ||
							it.platform.name
								.lowercase()
								.contains(filter)
					}.map { script ->
						Script(
							description = script.label,
							scriptText = formatScripts(script),
							originalScript = script,
							isExpanded = _uiState.value.scripts.any {
								(it.description == script.label || it.scriptText == formatScripts(script)) && it.isExpanded
							},
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

	private fun onExecuteScript(script: Script) {
		if (script.originalScript.platform == ScriptsRepository.Platform.DESKTOP) {
			viewModelScope.launch(ioDispatcher) {
				executeScriptUseCase(script = script.originalScript)
			}
		} else {
			_uiState.value.connectedDevices
				.filter {
					it.isSelected
				}.forEach { device ->
					viewModelScope.launch(ioDispatcher) {
						executeScriptUseCase(script = script.originalScript, selectedDevice = device.id)
					}
				}
		}
	}

	private fun onExecuteScriptText(
		script: String,
		platform: ScriptsRepository.Platform,
	) {
		viewModelScope.launch(ioDispatcher) {
			if (platform == ScriptsRepository.Platform.DESKTOP) {
				executeScript(
					script = script,
					platform = platform,
					device = "",
				)
			} else {
				_uiState.value.connectedDevices
					.filter {
						it.isSelected
					}.forEach { device ->
						executeScript(
							script = script,
							platform = platform,
							device = device.id,
						)
					}
			}
		}
	}

	private suspend fun executeScript(
		script: String,
		platform: ScriptsRepository.Platform,
		device: String = "",
	) {
		executeScriptUseCase(
			script = ScriptsRepository.Script(
				label = "entered by terminal script",
				scripts = listOf(script),
				platform = platform,
			),
			selectedDevice = device,
		)
	}

	private fun onExpandScript(script: Script) {
		_uiState.update { oldState ->
			oldState.copy(
				scripts = oldState.scripts.map { currentScript ->
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

	private suspend fun onFilterScripts(filter: String) {
		savePreferenceUseCase(SCRIPT_FILTER_PREF_KEY, filter)
		onRefreshScripts(jsonParseResult = getUserScriptsUseCase())
	}

	private fun onOpenScriptFile() {
		viewModelScope.launch(ioDispatcher) {
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

	private fun formatScripts(script: ScriptsRepository.Script): String = script.scripts.joinToString("\n")

	private fun mapHint(parsingMetaData: ScriptsRepository.ParsingMetaData?): Hint? {
		if (parsingMetaData == null) {
			return null
		}

		return when (parsingMetaData) {
			is ScriptsRepository.ParsingMetaData.MultiScriptsHint -> Hint.MultiScripts
			is ScriptsRepository.ParsingMetaData.OldScriptFieldHint -> Hint.OldScriptFieldHint
			is ScriptsRepository.ParsingMetaData.ParsingError -> Hint.Error(throwable = parsingMetaData.throwable)
		}
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

		data class OnFilterScripts(
			val filter: String,
		) : Event
	}

	data class UiState(
		val connectedDevices: List<Device> = emptyList(),
		val scripts: List<Script> = emptyList(),
		val logging: List<String> = emptyList(),
		val toolSections: List<ToolSection> = ToolSection.entries,
		val filter: String = "",
		val hint: Hint? = null,
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

	companion object {
		const val SCRIPT_FILTER_PREF_KEY = "SCRIPT_FILTER"
	}
}
