package de.joz.appcommander.domain

import org.koin.core.annotation.Factory

@Factory
class GetPreferenceUseCase(
    private val preferencesRepository: PreferencesRepository,
) {
    suspend fun get(
        key: String,
        defaultValue: Boolean = false,
    ): Boolean = preferencesRepository.get(key, defaultValue)

    suspend fun get(
        key: String,
        defaultValue: Int = 0,
    ): Int = preferencesRepository.get(key, defaultValue)
}
