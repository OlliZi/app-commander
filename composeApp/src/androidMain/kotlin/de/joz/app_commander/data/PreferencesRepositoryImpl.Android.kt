package de.joz.app_commander.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class DataStoreAndroidImpl(
    private val context: Context,
) {
    fun createDataStore() =
        createDataStore {
            context.filesDir.resolve(SIMPLE_PREF_FILE_NAME).absolutePath
        }
}

actual fun getDataStore(fileName: String): DataStore<Preferences> {
    TODO("USE DI KOIN TO FIX")
}