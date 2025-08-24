package de.joz.app_commander.data

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

actual fun getDataStore(fileName: String): DataStore<Preferences> {
    return createDataStore {
        File(baseDirectory, fileName).absolutePath
    }
}