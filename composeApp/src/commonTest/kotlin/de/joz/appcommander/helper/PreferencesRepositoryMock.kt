package de.joz.appcommander.helper

import de.joz.appcommander.domain.PreferencesRepository

class PreferencesRepositoryMock() : PreferencesRepository {

    var lastStoredValues = mutableMapOf<String, Any>()

    override suspend fun get(key: String, defaultValue: Boolean): Boolean {
        return lastStoredValues.get(key) as? Boolean ?: defaultValue
    }

    override suspend fun get(key: String, defaultValue: Int): Int {
        return lastStoredValues.get(key) as? Int ?: defaultValue
    }

    override suspend fun <T> store(key: String, value: T) {
        lastStoredValues[key] = value as Any
    }
}