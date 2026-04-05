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
				jsonScriptForUi = fromScripts(it),
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
				is Event.OnExpandJson -> onExpandJson(item = event.item, wholeObject = event.wholeObject)
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

	private fun onExpandJson(
		item: JsonItem,
		wholeObject: Boolean,
	) {
		_uiState.update { oldState ->
			val newList = oldState.jsonScriptForUi.map {
				if (it == item) {
					if (wholeObject) {
						val isWholeObjectExpanded = !item.isWholeObjectExpanded
						it.copy(
							isWholeObjectExpanded = isWholeObjectExpanded,
							iconWholeObject = isWholeObjectExpanded.toIcon(jsonType = JsonType.OBJECT),
							collapseScript = if (isWholeObjectExpanded) {
								item.originalScript.copy(
									scripts = if (item.isScriptSectionExpanded) {
										item.originalScript.scripts
									} else {
										emptyList()
									},
								)
							} else {
								null
							},
						)
					} else {
						val isScriptSectionExpanded = !item.isScriptSectionExpanded
						it.copy(
							isScriptSectionExpanded = isScriptSectionExpanded,
							iconArraySection = isScriptSectionExpanded.toIcon(jsonType = JsonType.ARRAY),
							collapseScript = item.originalScript.copy(
								scripts = if (isScriptSectionExpanded) {
									item.originalScript.scripts
								} else {
									emptyList()
								},
							),
						)
					}
				} else {
					it
				}
			}

			oldState.copy(
				json = jsonParser
					.encodeToString(
						newList.map {
							it.collapseScript
						},
					).replace("null", "{}"),
				jsonScriptForUi = newList,
			)
		}
	}

	sealed interface Event {
		data object OnNavigateBack : Event

		data object OnSaveScript : Event

		data object OnOpenScriptFile : Event

		data class OnExpandJson(
			val item: JsonItem,
			val wholeObject: Boolean,
		) : Event

		data class OnJsonChange(
			val json: String,
		) : Event
	}

	data class UiState(
		val json: String,
		val isJsonValid: Boolean = true,
		val jsonValidMessage: String = "",
		val jsonScriptForUi: List<JsonItem> = emptyList(),
	)

	data class JsonItem(
		val iconWholeObject: String,
		val iconArraySection: String,
		val isWholeObjectExpanded: Boolean,
		val isScriptSectionExpanded: Boolean,
		internal val originalScript: ScriptsRepository.Script,
		internal val collapseScript: ScriptsRepository.Script?,
	)

	enum class JsonType(
		val type: String,
	) {
		OBJECT(type = " {}"),
		ARRAY(type = " []"),
	}

	companion object {
		private const val ARROW_DOWN = "↓"
		private const val ARROW_UP = "↑"

		fun Boolean.toIcon(jsonType: JsonType) =
			when (jsonType) {
				JsonType.OBJECT -> if (this) ARROW_DOWN.plus(jsonType.type) else ARROW_UP.plus(jsonType.type)
				JsonType.ARRAY -> if (this) ARROW_DOWN.plus(jsonType.type) else ARROW_UP.plus(jsonType.type)
			}

		fun fromScripts(scripts: List<ScriptsRepository.Script>): List<JsonItem> =
			scripts.map { script ->
				fromScript(script = script)
			}

		fun fromScript(script: ScriptsRepository.Script): JsonItem {
			val allExpanded = true
			return JsonItem(
				iconWholeObject = allExpanded.toIcon(jsonType = JsonType.OBJECT),
				iconArraySection = allExpanded.toIcon(jsonType = JsonType.ARRAY),
				isWholeObjectExpanded = allExpanded,
				isScriptSectionExpanded = allExpanded,
				originalScript = script,
				collapseScript = script,
			)
		}
	}
}
