package de.joz.appcommander.domain.script

import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import org.koin.core.annotation.Factory

@Factory
class FilterScriptUseCase(
	private val getPreferenceUseCase: GetPreferenceUseCase,
) {
	suspend operator fun invoke(scripts: List<ScriptsRepository.Script>): FilterResult {
		val filterText = getPreferenceUseCase.get(SCRIPT_FILTER_PREF_KEY, "").lowercase()
		val filteredScripts = scripts.filter { script ->
			val filterLabel = filterLabel(script, filterText)
			val filterPlatform = filterPlatform(script, filterText)
			val filterScripts = filterScripts(script, filterText)
			val filterComment = filterComment(script, filterText)
			return@filter filterLabel or filterPlatform or filterScripts or filterComment
		}

		return FilterResult(
			scripts = filteredScripts,
			filterText = filterText,
		)
	}

	private fun filterLabel(
		script: ScriptsRepository.Script,
		filter: String,
	): Boolean = script.label.filterLowerCase(filter)

	private fun filterPlatform(
		script: ScriptsRepository.Script,
		filter: String,
	): Boolean = script.platform.name.filterLowerCase(filter)

	private fun filterScripts(
		script: ScriptsRepository.Script,
		filter: String,
	): Boolean =
		script.scripts.any { script ->
			val scriptFilter = script.script.filterLowerCase(filter)
			val commentFilter = when (script) {
				is ScriptsRepository.ScriptCode.CommentedScript -> {
					script.comment.filterLowerCase(filter)
				}

				is ScriptsRepository.ScriptCode.Script -> {
					false
				}
			}

			return@any scriptFilter or commentFilter
		}

	private fun filterComment(
		script: ScriptsRepository.Script,
		filter: String,
	): Boolean =
		if (script.comment == null) {
			false
		} else {
			script.comment.filterLowerCase(filter)
		}

	private fun String.filterLowerCase(filter: String) = lowercase().contains(filter)

	data class FilterResult(
		val scripts: List<ScriptsRepository.Script>,
		val filterText: String,
	)

	companion object {
		const val SCRIPT_FILTER_PREF_KEY = "SCRIPT_FILTER"
	}
}
