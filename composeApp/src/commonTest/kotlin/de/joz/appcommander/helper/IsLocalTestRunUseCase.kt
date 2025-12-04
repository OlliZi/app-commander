package de.joz.appcommander.helper

class IsLocalTestRunUseCase {
	operator fun invoke(): Boolean {
		val isDebuggerEnabled = System.getenv("DEBUGGER_ENABLED")
		val cfBundleIdentifier = System.getenv("__CFBundleIdentifier") ?: ""
		val isJenkinsEnvironment =
			System.getenv().any {
				it.key.lowercase().contains("jenkins") || it.value.lowercase().contains("jenkins")
			}

		return isDebuggerEnabled.toBoolean() || cfBundleIdentifier.contains("com.google.android.studio") ||
			!isJenkinsEnvironment
	}
}
