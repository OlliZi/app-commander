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
	override val uiState = _uiState.asStateFlow()

	override fun onEvent(event: Event) {
		viewModelScope.launch(mainDispatcher) {
			when (event) {
				is Event.OnNavigateBack -> onNavigateBack()
				is Event.OnSelectPlatform -> onSelectPlatform(event.platform)
				is Event.OnChangeScript -> onChangeScript(event.script)
				is Event.OnChangeScriptName -> onChangeScriptName(event.scriptName)
				is Event.OnExecuteScript -> onExecuteScript()
				is Event.OnSaveScript -> onSaveScript()
				is Event.OnRemoveScript -> onRemoveScript()
			}
		}
	}

	private fun onNavigateBack() {
		navController.navigateUp()
	}

	private fun onSelectPlatform(platform: ScriptsRepository.Platform) {
		_uiState.update { oldState ->
			oldState.copy(
				selectedPlatform = platform,
			)
		}
	}

	private fun onChangeScript(script: String) {
		_uiState.update { oldState ->
			oldState.copy(
				script = script,
			)
		}
	}

	private fun onChangeScriptName(scriptName: String) {
		_uiState.update { oldState ->
			oldState.copy(
				scriptName = scriptName,
			)
		}
	}

	private fun onExecuteScript() {
		viewModelScope.launch(ioDispatcher) {
			executeScriptUseCase(
				script =
					ScriptsRepository.Script(
						label = _uiState.value.scriptName,
						script = _uiState.value.script,
						platform = _uiState.value.selectedPlatform,
					),
				selectedDevice = "TODO",
			)
		}
	}

	private fun onSaveScript() {
		viewModelScope.launch(ioDispatcher) {
			val scriptToSave =
				ScriptsRepository.Script(
					label = _uiState.value.scriptName,
					script = _uiState.value.script,
					platform = _uiState.value.selectedPlatform,
				)
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
						label = _uiState.value.scriptName,
						script = _uiState.value.script,
						platform = _uiState.value.selectedPlatform,
					),
			)
		}

		onNavigateBack()
	}

	private fun mapToUiState(script: ScriptsRepository.Script?): UiState =
		UiState(
			scriptName = script?.label.orEmpty(),
			script = script?.script.orEmpty(),
			selectedPlatform = script?.platform ?: ScriptsRepository.Platform.ANDROID,
		)

	sealed interface Event {
		data object OnNavigateBack : Event

		data object OnSaveScript : Event

		data object OnRemoveScript : Event

		data object OnExecuteScript : Event

		data class OnChangeScript(
			val script: String,
		) : Event

		data class OnChangeScriptName(
			val scriptName: String,
		) : Event

		data class OnSelectPlatform(
			val platform: ScriptsRepository.Platform,
		) : Event
	}

	data class UiState(
		val script: String = "",
		val scriptName: String = "",
		val selectedPlatform: ScriptsRepository.Platform = ScriptsRepository.Platform.ANDROID,
	)
}
