package de.joz.appcommander.domain.preference

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetPreferenceUseCase(
	private val preferencesRepository: PreferencesRepository,
) {
	suspend fun get(
		key: String,
		defaultValue: Boolean = false,
	): Boolean = preferencesRepository.get(key, defaultValue)

	suspend fun getAsFlow(vararg keys: String): Flow<List<ChangedPreference>> =
		preferencesRepository.getAsFlow(
			keys = keys,
		)

	suspend fun get(
		key: String,
		defaultValue: Int = 0,
	): Int = preferencesRepository.get(key, defaultValue)
}
