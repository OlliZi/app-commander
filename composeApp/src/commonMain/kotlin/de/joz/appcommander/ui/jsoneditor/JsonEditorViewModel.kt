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
		jsonParser.encodeToString(getUserScriptsUseCase().scripts).let {
			UiState(
				json = it,
				jsonMenuItems = buildJsonMenuBar(it),
				jsonScriptForUi = getUserScriptsUseCase().scripts.map {
					JsonScriptForUi(
						icon = "+",
						isExpanded = true,
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

	private fun onExpandJson(item: JsonObjectItem) {
		_uiState.update { oldState ->
			oldState.copy(
				jsonMenuItems = oldState.jsonMenuItems.map { menuItem ->
					if (menuItem == item) {
						item.copy(
							icon = if (item.isExpanded) ARROW_UP else ARROW_DOWN,
							isExpanded = item.isExpanded.not(),
						)
					} else {
						menuItem
					}
				},
			)
		}
	}

	private fun mapJsonByMenuBar() {
		_uiState.update { oldState ->
			oldState.copy()
		}
	}

	private fun computeType(token: String): JsonType =
		when {
			token.startsWith("{") -> JsonType.OBJECT
			token.endsWith("[") -> JsonType.ARRAY
			else -> JsonType.CONTENT
		}

	private fun buildJsonMenuBar(json: String): List<JsonObjectItem> {
		var currentVisitedJsonStringCount = 0
		return json.split("\n").mapIndexed { index, string ->
			currentVisitedJsonStringCount += string.length + 1 // add linebreak

			val cleaned = string.trim()
			val type = computeType(cleaned)

			JsonObjectItem(
				icon = if (type == JsonType.CONTENT) "" else ARROW_DOWN,
				index = index,
				content = string,
				indentation = string.length,
				currentVisitedJsonStringCount = currentVisitedJsonStringCount,
				isExpanded = true,
				type = type,
			)
		}
	}

	sealed interface Event {
		data object OnNavigateBack : Event

		data object OnSaveScript : Event

		data object OnOpenScriptFile : Event

		data class OnExpandJson(
			val item: JsonObjectItem,
		) : Event

		data class OnJsonChange(
			val json: String,
		) : Event
	}

	data class UiState(
		val json: String,
		val isJsonValid: Boolean = true,
		val jsonValidMessage: String = "",
		val jsonMenuItems: List<JsonObjectItem> = emptyList(),
		val jsonScriptForUi: List<JsonScriptForUi> = emptyList(),
	)

	data class JsonObjectItem(
		val icon: String,
		val index: Int,
		val content: String,
		val indentation: Int,
		val currentVisitedJsonStringCount: Int,
		val isExpanded: Boolean,
		val type: JsonType,
	)

	enum class JsonType {
		OBJECT,
		ARRAY,
		CONTENT,
	}

	data class JsonScriptForUi(
		val icon: String,
		val isExpanded: Boolean,
		val isScriptSectionExpanded: Boolean,
		internal val script: ScriptsRepository.Script,
		internal val collapseScript: ScriptsRepository.Script,
	)

	companion object {
		const val ARROW_DOWN = "↓"
		const val ARROW_UP = "↑"
	}
}
