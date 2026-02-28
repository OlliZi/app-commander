package de.joz.appcommander.ui.misc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.confirmation_no
import de.joz.appcommander.resources.confirmation_yes
import de.joz.appcommander.resources.edit_confirmation_remove
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.internalpreviews.PreviewRenderContainer
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun Confirmation(
	show: Boolean,
	title: String,
	onOkSelected: () -> Unit,
	onDismissRequest: () -> Unit,
) {
	var closeDialog by remember { mutableStateOf(show) }
	AnimatedVisibility(
		visible = show,
	) {
		AlertDialog(
			onDismissRequest = {
				closeDialog = false
				onDismissRequest.invoke()
			},
			title = {
				TextLabel(
					text = title,
					textLabelType = TextLabelType.HeadlineSmall,
				)
			},
			confirmButton = {
				Button(onClick = {
					closeDialog = false
					onOkSelected.invoke()
				}) {
					TextLabel(
						text = stringResource(Res.string.confirmation_yes),
						textLabelType = TextLabelType.BodyLarge,
					)
				}
			},
			dismissButton = {
				Button(onClick = {
					closeDialog = false
					onDismissRequest.invoke()
				}) {
					TextLabel(
						text = stringResource(Res.string.confirmation_no),
						textLabelType = TextLabelType.BodyLarge,
					)
				}
			},
		)
	}
}

@Preview
@Composable
internal fun PreviewConfirmation() {
	PreviewRenderContainer { previewData ->
		PreviewConfirmation(previewData)
	}
}

@Preview
@Composable
internal fun PreviewConfirmation(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = previewData.uiState,
	) {
		Confirmation(
			show = true,
			title = stringResource(Res.string.edit_confirmation_remove),
			onOkSelected = {},
			onDismissRequest = {},
		)
	}
}
