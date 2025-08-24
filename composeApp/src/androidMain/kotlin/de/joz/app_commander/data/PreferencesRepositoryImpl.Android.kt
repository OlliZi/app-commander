package de.joz.app_commander.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class DataStoreAndroidImpl {
    fun createDataStore() =
        createDataStore {
            dummycontext!!.filesDir.resolve(SIMPLE_PREF_FILE_NAME).absolutePath
        }

    companion object {
        var dummycontext: Context? = null
    }
}

actual fun getDataStore(fileName: String): DataStore<Preferences> {
    return DataStoreAndroidImpl().createDataStore()
}