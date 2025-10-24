package de.joz.appcommander.ui.misc.screenshot

import java.io.File

sealed interface ScreenshotResult {
	data class Success(
		val screenshot: File,
	) : ScreenshotResult

	data class Failure(
		val error: Throwable,
	) : ScreenshotResult
}
