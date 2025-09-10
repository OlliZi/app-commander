package de.joz.appcommander.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual fun getDataStore(fileName: String): DataStore<Preferences> =
    PreferencesRepositoryJvmImpl(fileName).dataStore

internal class PreferencesRepositoryJvmImpl(
    private val fileName: String,
) {
    private val baseDirectory: String by lazy {
        val baseFile = File(System.getProperty("user.home"), ".app_commander")
        if (!baseFile.exists()) {
            baseFile.mkdirs()
        }
        baseFile.absolutePath
    }

    val dataStore: DataStore<Preferences> by lazy {
        createDataStore {
            File(baseDirectory, fileName).absolutePath
        }
    }
}
