package de.joz.app_commander.domain

class SavePreferenceUseCase(
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(key: String, value: Any) {
        preferencesRepository.store(key, value)
    }
}