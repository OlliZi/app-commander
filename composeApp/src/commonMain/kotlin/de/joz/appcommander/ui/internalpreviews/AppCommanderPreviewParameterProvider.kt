package de.joz.appcommander.ui.internalpreviews

import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider

internal open class AppCommanderPreviewParameterProvider<T>(
	private val previewParameter: List<PreviewData<T>>,
) : CollectionPreviewParameterProvider<PreviewData<T>>(
		previewParameter,
	) {
	override fun getDisplayName(index: Int) = previewParameter[index].label
}

internal data class PreviewData<T>(
	val label: String,
	val uiState: T,
) {
	companion object {
		fun createThemeDarkMode(darkMode: Boolean) =
			if (darkMode) {
				darkMode()
			} else {
				lightMode()
			}

		private fun darkMode() =
			PreviewData(
				label = "Dark",
				uiState = true,
			)

		private fun lightMode() =
			PreviewData(
				label = "Light",
				uiState = false,
			)
	}
}
