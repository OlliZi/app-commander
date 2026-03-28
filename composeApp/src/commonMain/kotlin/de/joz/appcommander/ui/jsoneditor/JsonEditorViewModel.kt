package de.joz.appcommander.ui.jsoneditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.joz.appcommander.IODispatcher
import de.joz.appcommander.MainDispatcher
import de.joz.appcommander.domain.script.GetUserScriptsUseCase
import de.joz.appcommander.domain.script.OpenScriptFileUseCase
import de.joz.appcommander.ui.misc.UnidirectionalDataFlowViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class JsonEditorViewModel(
	@InjectedParam private val navController: NavController,
	getUserScriptsUseCase: GetUserScriptsUseCase,
	private val json: Json,
	private val openScriptFileUseCase: OpenScriptFileUseCase,
	@MainDispatcher private val mainDispatcher: CoroutineDispatcher,
	@IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel(),
	UnidirectionalDataFlowViewModel<JsonEditorViewModel.UiState, JsonEditorViewModel.Event> {
	private val _uiState = MutableStateFlow(
		UiState(
			json = json.encodeToString(getUserScriptsUseCase().scripts),
		),
	)

	override val uiState = _uiState.asStateFlow()

	override fun onEvent(event: Event) {
		viewModelScope.launch(mainDispatcher) {
			when (event) {
				is Event.OnNavigateBack -> onNavigateBack()
				is Event.OnJsonChange -> onJsonChange()
				is Event.OnSaveScript -> onSaveScript()
				is Event.OnOpenScriptFile -> onOpenScriptFile()
			}
		}
	}

	private fun onNavigateBack() {
		navController.navigateUp()
	}

	private fun onJsonChange() {
	}

	private fun onSaveScript() {}

	private fun onOpenScriptFile() {
		viewModelScope.launch(ioDispatcher) {
			openScriptFileUseCase()
		}
	}

	sealed interface Event {
		data object OnNavigateBack : Event

		data object OnSaveScript : Event

		data object OnOpenScriptFile : Event

		data class OnJsonChange(
			val json: String,
		) : Event
	}

	data class UiState(
		val json: String,
	)
}
