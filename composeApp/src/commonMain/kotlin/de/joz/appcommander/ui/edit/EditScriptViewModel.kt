package de.joz.appcommander.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.joz.appcommander.IODispatcher
import de.joz.appcommander.MainDispatcher
import de.joz.appcommander.domain.script.ExecuteScriptUseCase
import de.joz.appcommander.domain.script.GetScriptIdUseCase
import de.joz.appcommander.domain.script.GetUserScriptByKeyUseCase
import de.joz.appcommander.domain.script.RemoveUserScriptUseCase
import de.joz.appcommander.domain.script.SaveUserScriptUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
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
	private val getScriptIdUseCase: GetScriptIdUseCase,
	private val executeScriptUseCase: ExecuteScriptUseCase,
	private val saveUserScriptUseCase: SaveUserScriptUseCase,
	private val removeUserScriptUseCase: RemoveUserScriptUseCase,
	@MainDispatcher private val mainDispatcher: CoroutineDispatcher,
	@IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel(),
	UnidirectionalDataFlowViewModel<EditScriptViewModel.UiState, EditScriptViewModel.Event> {
	private val _uiState =
		MutableStateFlow(
			mapToUiState(getUserScriptByKeyUseCase(scriptKey)),
		)
	private val originalUiState = _uiState.value

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
			scripts =
				_uiState.value.scriptUiState.scripts.mapIndexed { oldIndex, oldScript ->
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
			scripts =
				_uiState.value.scriptUiState.scripts
					.toMutableList()
					.apply {
						add(index + 1, "<enter new script>")
					}.toList(),
		)
	}

	private fun onRemoveSubScript(index: Int) {
		updateUiState(
			scripts =
				if (_uiState.value.scriptUiState.scripts.size == 1) {
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
			val newScript =
				oldState.scriptUiState.copy(
					scriptName = scriptName ?: oldState.scriptUiState.scriptName,
					scripts = scripts ?: oldState.scriptUiState.scripts,
					selectedPlatform = selectedPlatform ?: oldState.scriptUiState.selectedPlatform,
				)
			oldState.copy(
				hasChanges = newScript != originalUiState.scriptUiState,
				scriptUiState = newScript,
			)
		}
	}

	private fun onExecuteSingleScript(script: String) {
		viewModelScope.launch(ioDispatcher) {
			executeScriptUseCase(
				script =
					ScriptsRepository.Script(
						label = _uiState.value.scriptUiState.scriptName,
						scripts = listOf(script),
						platform = _uiState.value.scriptUiState.selectedPlatform,
					),
				selectedDevice = "TODO",
			)
		}
	}

	private fun onExecuteAllScripts() {
		viewModelScope.launch(ioDispatcher) {
			executeScriptUseCase(
				script = toScriptsRepositoryScript(),
				selectedDevice = "TODO",
			)
		}
	}

	private fun onSaveScript() {
		viewModelScope.launch(ioDispatcher) {
			val scriptToSave = toScriptsRepositoryScript()
			saveUserScriptUseCase(
				script = scriptToSave,
				scriptKey = scriptKey,
			)

			scriptKey = getScriptIdUseCase(scriptToSave)
		}

		onNavigateBack()
	}

	private fun onRemoveScript() {
		viewModelScope.launch(ioDispatcher) {
			removeUserScriptUseCase(
				script =
					ScriptsRepository.Script(
						label = _uiState.value.scriptUiState.scriptName,
						scripts = _uiState.value.scriptUiState.scripts,
						platform = _uiState.value.scriptUiState.selectedPlatform,
					),
			)
		}

		onNavigateBack()
	}

	private fun mapToUiState(script: ScriptsRepository.Script?): UiState =
		UiState(
			hasChanges = false,
			scriptUiState =
				ScriptUiState(
					scriptName = script?.label.orEmpty(),
					scripts = script?.scripts ?: listOf(""),
					selectedPlatform = script?.platform ?: ScriptsRepository.Platform.ANDROID,
				),
		)

	sealed interface Event {
		data object OnNavigateBack : Event

		data object OnSaveScript : Event

		data object OnRemoveScript : Event

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
		val hasChanges: Boolean = false,
		val scriptUiState: ScriptUiState = ScriptUiState(),
	)

	data class ScriptUiState(
		val scripts: List<String> = emptyList(),
		val scriptName: String = "",
		val selectedPlatform: ScriptsRepository.Platform = ScriptsRepository.Platform.ANDROID,
	)

	private fun toScriptsRepositoryScript() =
		ScriptsRepository.Script(
			label = _uiState.value.scriptUiState.scriptName,
			scripts = _uiState.value.scriptUiState.scripts,
			platform = _uiState.value.scriptUiState.selectedPlatform,
		)
}
