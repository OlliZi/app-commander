package de.joz.appcommander.data

import java.io.File

actual fun getPreferenceFileStorePath(fileName: String): String {
	val baseFile = File(System.getProperty("user.home"), ".app_commander")
	if (!baseFile.exists()) {
		baseFile.mkdirs()
	}
	return File(baseFile.absolutePath, fileName).absolutePath
}
