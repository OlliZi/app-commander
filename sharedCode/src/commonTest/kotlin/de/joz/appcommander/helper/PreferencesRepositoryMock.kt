package de.joz.appcommander.helper

import de.joz.appcommander.domain.preference.ChangedPreference
import de.joz.appcommander.domain.preference.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PreferencesRepositoryMock : PreferencesRepository {
	private val prefsFlow = MutableStateFlow<List<ChangedPreference>>(emptyList())
	var lastStoredValues = mutableMapOf<String, Any>()

	override suspend fun get(
		key: String,
		defaultValue: Boolean,
	): Boolean = lastStoredValues[key] as? Boolean ?: defaultValue

	override suspend fun get(
		key: String,
		defaultValue: String,
	): String = lastStoredValues[key] as? String ?: defaultValue

	override suspend fun get(
		key: String,
		defaultValue: Int,
	): Int = lastStoredValues[key] as? Int ?: defaultValue

	override suspend fun getAsFlow(vararg keys: String): Flow<List<ChangedPreference>> = prefsFlow

	override suspend fun <T> store(
		key: String,
		value: T,
	) {
		prefsFlow.value += ChangedPreference(key = key, value = value)
		lastStoredValues[key] = value as Any
	}
}
