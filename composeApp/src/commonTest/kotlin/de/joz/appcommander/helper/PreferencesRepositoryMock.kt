package de.joz.appcommander.helper

import de.joz.appcommander.domain.preference.ChangedPreference
import de.joz.appcommander.domain.preference.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class PreferencesRepositoryMock : PreferencesRepository {
	var lastStoredValues = mutableMapOf<String, Any>()

	override suspend fun get(
		key: String,
		defaultValue: Boolean,
	): Boolean = lastStoredValues[key] as? Boolean ?: defaultValue

	override suspend fun getAsFlow(vararg moreKeys: String): Flow<List<ChangedPreference>> {
		TODO("Not yet implemented")
	}

	override suspend fun get(
		key: String,
		defaultValue: Int,
	): Int = lastStoredValues[key] as? Int ?: defaultValue

	override suspend fun <T> store(
		key: String,
		value: T,
	) {
		lastStoredValues[key] = value as Any
	}
}
