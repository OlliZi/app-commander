package de.joz.app_commander.domain

import org.koin.core.annotation.Factory

@Factory
class SavePreferenceUseCase(
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(key: String, value: Any) {
        preferencesRepository.store(key, value)
    }
}