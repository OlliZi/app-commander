package de.joz.appcommander.helper.screenshot

import java.io.File

sealed interface ScreenshotResult {
	data class Success(
		val screenshot: File,
	) : ScreenshotResult

	data class Failure(
		val error: Throwable,
	) : ScreenshotResult
}
