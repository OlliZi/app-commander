package de.joz.appcommander.data

import java.io.File

actual fun getPreferenceFileStorePath(fileName: String): String =
	File("/data/data/de.joz.appcommander/files", fileName).absolutePath
