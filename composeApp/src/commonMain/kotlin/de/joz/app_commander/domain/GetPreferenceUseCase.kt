package de.joz.app_commander.domain

class GetPreferenceUseCase(
    private val preferencesRepository: PreferencesRepository,
) {
    suspend fun get(key: String, defaultValue: Boolean = false): Boolean {
        return preferencesRepository.get(key, defaultValue)
    }

    suspend fun get(key: String, defaultValue: Int = 0): Int {
        return preferencesRepository.get(key, defaultValue)
    }
}