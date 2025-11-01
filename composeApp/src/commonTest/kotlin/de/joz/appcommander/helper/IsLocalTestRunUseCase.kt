package de.joz.appcommander.helper

class IsLocalTestRunUseCase {
	operator fun invoke(): Boolean {
		val systemEnv = System.getenv()
		if (systemEnv == null || systemEnv.isEmpty()) {
			return false
		}

		println("### env: $systemEnv")

		val isDebuggerEnabled = System.getenv("DEBUGGER_ENABLED")
		val cfBundleIdentifier = System.getenv("__CFBundleIdentifier")

		return isDebuggerEnabled.toBoolean() || cfBundleIdentifier.contains("com.google.android.studio")
	}
}
