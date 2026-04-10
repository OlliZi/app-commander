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
				json = convertScriptsToUi(scripts = it, jsonParser = jsonParser),
				jsonScriptForUi = fromScriptsToJsonMenu(it),
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
				val scriptObjects = json.split("},").filter { it.isNotBlank() }.map { it.trimStart() }.map {
					if (!it.endsWith("}")) {
						"$it}"
					} else {
						it
					}
				}
				if (scriptObjects.size != oldState.jsonScriptForUi.size) {
					resetStrategyAfterChange(oldState)
				} else {
					// find changed scripts
					oldState.copy(
						json = json,
						isJsonValid = true,
						jsonValidMessage = "",
						jsonScriptForUi = oldState.jsonScriptForUi.mapIndexed { index, scriptObject ->
							val changedScript = scriptObjects[index]
							val originalJson = convertScriptToUi(scriptObject.collapseScript, jsonParser)
							if (changedScript != originalJson) {
								val changedScriptJson = jsonParser.decodeFromString<ScriptsRepository.Script>(
									changedScript,
								)
								scriptObject.copy(
									originalScript = scriptObject.originalScript.copy(
										label = changedScriptJson.label,
										platform = changedScriptJson.platform,
										scripts = changedScriptJson.scripts,
									),
									collapseScript = changedScriptJson,
									isWholeObjectExpanded = true,
								)
							} else {
								scriptObject
							}
						},
					)
				}
			}.getOrElse {
				fallbackStrategy(oldState, json, it)
			}
		}
	}

	private fun onSaveScript() {
		saveUserScriptsUseCase.invoke(
			scripts = wrapToJsonObject(
				json = convertScriptsToUi(
					_uiState.value.jsonScriptForUi.map {
						it.originalScript
					},
					jsonParser = jsonParser,
				),
				jsonParser = jsonParser,
			),
		)
		onNavigateBack()
	}

	private fun resetStrategyAfterChange(oldState: UiState): UiState {
		val originalScriptList = oldState.jsonScriptForUi.map {
			fromScriptToJsonMenu(script = it.originalScript)
		}
		return oldState.copy(
			json = convertScriptsToUi(originalScriptList.map { it.originalScript }, jsonParser),
			isJsonValid = false,
			jsonValidMessage = "Invalid JSON -> reset all JSON objects",
			jsonScriptForUi = originalScriptList,
		)
	}

	private fun fallbackStrategy(
		oldState: UiState,
		json: String,
		throwable: Throwable,
	): UiState =
		oldState.copy(
			json = json,
			isJsonValid = false,
			jsonValidMessage = throwable.localizedMessage,
		)

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
							collapseScript = if (isWholeObjectExpanded) {
								item.originalScript.copy(
									scripts = item.originalScript.scripts,
								)
							} else {
								null
							},
						)
					} else {
						it
					}
				} else {
					it
				}
			}

			oldState.copy(
				json = convertScriptsToUi(
					scripts = newList.map {
						it.collapseScript
					},
					jsonParser = jsonParser,
				),
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
		val isWholeObjectExpanded: Boolean,
		internal val originalScript: ScriptsRepository.Script,
		internal val collapseScript: ScriptsRepository.Script?,
	)

	companion object {
		private fun wrapToJsonObject(
			json: String,
			jsonParser: Json,
		): List<ScriptsRepository.Script> = jsonParser.decodeFromString<List<ScriptsRepository.Script>>("[$json]")

		private fun fromScriptToJsonMenu(script: ScriptsRepository.Script): JsonItem =
			JsonItem(
				isWholeObjectExpanded = true,
				originalScript = script,
				collapseScript = script,
			)

		private fun convertScriptToUi(
			script: ScriptsRepository.Script?,
			jsonParser: Json,
		): String = jsonParser.encodeToString(script).replace("null", "{}")

		fun convertScriptsToUi(
			scripts: List<ScriptsRepository.Script?>,
			jsonParser: Json,
		): String =
			scripts.joinToString(separator = ",\n") {
				convertScriptToUi(script = it, jsonParser = jsonParser)
			}

		fun fromScriptsToJsonMenu(scripts: List<ScriptsRepository.Script>): List<JsonItem> =
			scripts.map { script ->
				fromScriptToJsonMenu(script = script)
			}
	}
}
