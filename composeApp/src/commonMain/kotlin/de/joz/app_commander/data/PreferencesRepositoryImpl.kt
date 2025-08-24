package de.joz.app_commander.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import de.joz.app_commander.domain.PreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single

@Single
class PreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences> = getDataStore(),
) : PreferencesRepository {
    override suspend fun get(
        key: String,
        defaultValue: Boolean,
    ): Boolean {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: defaultValue
        }.first()
    }

    override suspend fun get(
        key: String,
        defaultValue: Int,
    ): Int {
        return dataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)] ?: defaultValue
        }.first()
    }

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

expect fun getDataStore(fileName: String = SIMPLE_PREF_FILE_NAME): DataStore<Preferences>

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = emptyList(),
        produceFile = {
            val filePath = producePath()
            if (filePath.endsWith(SIMPLE_PREF_FILE_EXTENSION)) {
                filePath
            } else {
                "$filePath$SIMPLE_PREF_FILE_EXTENSION"
            }.toPath()
        },
    )

internal const val SIMPLE_PREF_FILE_EXTENSION = ".preferences_pb"
internal const val SIMPLE_PREF_FILE_NAME = ".app_commander$SIMPLE_PREF_FILE_EXTENSION"
