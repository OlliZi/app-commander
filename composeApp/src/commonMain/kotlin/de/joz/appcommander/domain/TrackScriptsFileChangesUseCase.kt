package de.joz.appcommander.domain

import de.joz.appcommander.domain.preference.GetPreferenceUseCase
import de.joz.appcommander.ui.settings.SettingsViewModel.Companion.TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory
import kotlin.math.max
import kotlin.math.min

@Factory
class TrackScriptsFileChangesUseCase(
	private val getUserScriptsUseCase: GetUserScriptsUseCase,
	private val getPreferenceUseCase: GetPreferenceUseCase,
) {
	operator fun invoke(): Flow<ScriptsRepository.JsonParseResult> =
		flow {
			var scripts: ScriptsRepository.JsonParseResult? = null
			while (true) {
				val prefsValueInSeconds =
					getPreferenceUseCase.get(TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY, 1).toLong()
				val waitDelay = 1000 * min(10, max(1, prefsValueInSeconds))
				delay(waitDelay)

				val newLoadedScripts = getUserScriptsUseCase()
				if (scripts == null) {
					scripts = newLoadedScripts
				} else if (scripts.scripts != newLoadedScripts.scripts) {
					scripts = newLoadedScripts
					emit(newLoadedScripts)
				}
			}
		}
}
