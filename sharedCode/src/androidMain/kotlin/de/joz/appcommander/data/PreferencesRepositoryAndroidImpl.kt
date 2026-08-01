package de.joz.appcommander.data

import java.io.File

actual fun getPreferenceFileStorePath(fileName: String): String {
	// Simple path for now to allow compilation
	return File("/data/data/de.joz.appcommander/files", fileName).absolutePath
}
