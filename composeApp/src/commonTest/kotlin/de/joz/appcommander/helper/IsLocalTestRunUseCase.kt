package de.joz.appcommander.helper

class IsLocalTestRunUseCase {
	operator fun invoke(): Boolean {
		println("### env: ${System.getenv()}")

		val isDebuggerEnabled = System.getenv("DEBUGGER_ENABLED")
		val cfBundleIdentifier = System.getenv("__CFBundleIdentifier")

		return isDebuggerEnabled.toBoolean() || cfBundleIdentifier.contains("com.google.android.studio")
	}
}
