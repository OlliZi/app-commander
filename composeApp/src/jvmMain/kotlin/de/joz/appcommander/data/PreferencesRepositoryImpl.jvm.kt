package de.joz.appcommander.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

private val baseDirectory: String by lazy {
    val baseFile = File(System.getProperty("user.home"), ".app_commander")
    if (!baseFile.exists()) {
        baseFile.mkdirs()
    }
    baseFile.absolutePath
}

// TODO use DI
private var dataStore: DataStore<Preferences>? = null
actual fun getDataStore(fileName: String): DataStore<Preferences> {
    var localDataStore = dataStore
    if (localDataStore != null) {
        return localDataStore
    }
    localDataStore = createDataStore {
        File(baseDirectory, fileName).absolutePath
    }
    dataStore = localDataStore
    return localDataStore
}
