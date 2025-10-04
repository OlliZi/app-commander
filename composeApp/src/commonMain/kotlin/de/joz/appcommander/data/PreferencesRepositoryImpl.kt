package de.joz.appcommander.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import de.joz.appcommander.domain.PreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single

expect fun getPreferenceFileStorePath(fileName: String): String

@Single
internal class PreferencesRepositoryImpl(
	private val dataStore: DataStore<Preferences> = createDataStore(),
) : PreferencesRepository {
	override suspend fun get(
		key: String,
		defaultValue: Boolean,
	): Boolean =
		dataStore.data
			.map { preferences ->
				preferences[booleanPreferencesKey(key)] ?: defaultValue
			}.first()

	override suspend fun get(
		key: String,
		defaultValue: Int,
	): Int =
		dataStore.data
			.map { preferences ->
				preferences[intPreferencesKey(key)] ?: defaultValue
			}.first()

	override suspend fun <T> store(
		key: String,
		value: T,
	) {
		dataStore.edit { preferences ->
			when (value) {
				is Boolean -> preferences[booleanPreferencesKey(key)] = value
				is Int -> preferences[intPreferencesKey(key)] = value
				else -> throw IllegalArgumentException("Unsupported type")
			}
		}
	}
}

private fun createDataStore(): DataStore<Preferences> =
	PreferenceDataStoreFactory.createWithPath(
		corruptionHandler = null,
		migrations = emptyList(),
		produceFile = {
			getPreferenceFileStorePath(fileName = SIMPLE_PREF_FILE_NAME).toPath()
		},
	)

internal const val SIMPLE_PREF_FILE_NAME = "userprefs.preferences_pb"
