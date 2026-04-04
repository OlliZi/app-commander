package de.joz.appcommander.ui.jsoneditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.joz.appcommander.IODispatcher
import de.joz.appcommander.MainDispatcher
import de.joz.appcommander.domain.script.GetUserScriptsUseCase
import de.joz.appcommander.domain.script.OpenScriptFileUseCase
import de.joz.appcommander.domain.script.SaveUserScriptsUseCase
import de.joz.appcommander.domain.script.ScriptsRepository
import de.joz.appcommander.ui.misc.UnidirectionalDataFlowViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class JsonEditorViewModel(
	@InjectedParam private val navController: NavController,
	getUserScriptsUseCase: GetUserScriptsUseCase,
	private val jsonParser: Json,
	private val openScriptFileUseCase: OpenScriptFileUseCase,
	private val saveUserScriptsUseCase: SaveUserScriptsUseCase,
	@MainDispatcher private val mainDispatcher: CoroutineDispatcher,
	@IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel(),
	UnidirectionalDataFlowViewModel<JsonEditorViewModel.UiState, JsonEditorViewModel.Event> {
	private val _uiState = MutableStateFlow(
		getUserScriptsUseCase().scripts.let {
			UiState(
				json = jsonParser.encodeToString(it),
				jsonScriptForUi = it.map {
					JsonArrayItem(
						icon = ARROW_DOWN,
						isScriptSectionExpanded = true,
						script = it,
						collapseScript = it,
					)
				},
			)
		},
	)

	override val uiState = _uiState.asStateFlow()

	override fun onEvent(event: Event) {
		viewModelScope.launch(mainDispatcher) {
			when (event) {
				is Event.OnNavigateBack -> onNavigateBack()
				is Event.OnJsonChange -> onJsonChange(json = event.json)
				is Event.OnSaveScript -> onSaveScript()
				is Event.OnOpenScriptFile -> onOpenScriptFile()
				is Event.OnExpandJson -> onExpandJson(item = event.item)
			}
		}
	}

	private fun onNavigateBack() {
		navController.navigateUp()
	}

	private fun onJsonChange(json: String) {
		_uiState.update { oldState ->
			runCatching {
				jsonParser.decodeFromString<List<ScriptsRepository.Script>>(json)
				oldState.copy(
					json = json,
					isJsonValid = true,
					jsonValidMessage = "",
				)
			}.getOrElse {
				oldState.copy(
					json = json,
					isJsonValid = false,
					jsonValidMessage = it.localizedMessage,
				)
			}
		}
	}

	private fun onSaveScript() {
		saveUserScriptsUseCase.invoke(
			scripts = jsonParser.decodeFromString<List<ScriptsRepository.Script>>(_uiState.value.json),
		)
		onNavigateBack()
	}

	private fun onOpenScriptFile() {
		viewModelScope.launch(ioDispatcher) {
			openScriptFileUseCase()
		}
	}

	private fun onExpandJson(item: JsonArrayItem) {
		_uiState.update { oldState ->
			val isScriptSectionExpanded = !item.isScriptSectionExpanded

			val newList = oldState.jsonScriptForUi.map {
				if (it == item) {
					val newItem = it.copy(
						isScriptSectionExpanded = isScriptSectionExpanded,
						icon = if (isScriptSectionExpanded) ARROW_DOWN else ARROW_UP,
						collapseScript = item.collapseScript.copy(
							scripts = if (isScriptSectionExpanded) item.script.scripts else emptyList(),
						),
					)
					newItem
				} else {
					it
				}
			}

			val json = jsonParser.encodeToString(
				newList.map {
					it.collapseScript
				},
			)
			oldState.copy(
				json = json,
				jsonScriptForUi = newList,
			)
		}
	}

	sealed interface Event {
		data object OnNavigateBack : Event

		data object OnSaveScript : Event

		data object OnOpenScriptFile : Event

		data class OnExpandJson(
			val item: JsonArrayItem,
		) : Event

		data class OnJsonChange(
			val json: String,
		) : Event
	}

	data class UiState(
		val json: String,
		val isJsonValid: Boolean = true,
		val jsonValidMessage: String = "",
		val jsonScriptForUi: List<JsonArrayItem> = emptyList(),
	)

	data class JsonArrayItem(
		val icon: String,
		val isScriptSectionExpanded: Boolean,
		internal val script: ScriptsRepository.Script,
		internal val collapseScript: ScriptsRepository.Script,
	)

	companion object {
		const val ARROW_DOWN = "↓"
		const val ARROW_UP = "↑"
	}
}
