package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.internalpreviews.PreviewRenderContainer
import de.joz.appcommander.ui.theme.AppCommanderTheme

@Composable
fun MultiScriptInput(
	onExecuteScriptText: (String) -> Unit,
	onExecuteAllScriptsText: () -> Unit,
	multiScripts: List<String>,
	onChangeScriptText: (String, List<String>) -> Unit = { _, _ -> },
) {
	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		itemsIndexed(multiScripts) { index, script ->
			ScriptInput(
				script = script,
				onExecuteScriptText = onExecuteScriptText,
				onChangeScriptText = {
					onChangeScriptText(it, multiScripts)
				},
			)
			if (index < multiScripts.lastIndex) {
				ScriptDivider()
			}
		}
	}
}

@Composable
private fun ScriptDivider() {
	HorizontalDivider(
		modifier = Modifier.padding(vertical = 4.dp),
	)
}

@Preview
@Composable
internal fun PreviewMultiScriptInput() {
	PreviewRenderContainer { previewData ->
		PreviewMultiScriptInput(previewData)
	}
}

@Preview
@Composable
internal fun PreviewMultiScriptInput(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = previewData.uiState,
	) {
		MultiScriptInput(
			multiScripts = listOf("adb devices", "adb shell echo foo", "adb shell echo bar", "adb shell echo 123"),
			onExecuteScriptText = {},
			onExecuteAllScriptsText = { },
		)
	}
}
