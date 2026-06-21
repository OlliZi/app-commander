package de.joz.appcommander.ui.misc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.confirmation_no
import de.joz.appcommander.resources.confirmation_yes
import de.joz.appcommander.resources.edit_confirmation_remove
import de.joz.appcommander.ui.internalpreviews.PreviewRenderContainer
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Confirmation(
	confirmationData: ConfirmationData,
	onOkSelected: () -> Unit,
	onDismissRequest: () -> Unit,
) {
	AnimatedVisibility(
		visible = confirmationData.show,
	) {
		AlertDialog(
			onDismissRequest = {
				onDismissRequest.invoke()
			},
			title = {
				TextLabel(
					text = confirmationData.title?.let { stringResource(confirmationData.title) } ?: "",
					textLabelType = TextLabelType.HeadlineSmall,
				)
			},
			confirmButton = {
				Button(onClick = {
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

data class ConfirmationData(
	val show: Boolean,
	val title: StringResource? = null,
	val event: (() -> Unit)? = null,
)

@Preview
@Composable
internal fun PreviewConfirmation() {
	PreviewRenderContainer { previewData ->
		PreviewConfirmation(darkTheme = previewData.uiState)
	}
}

@Composable
internal fun PreviewConfirmation(darkTheme: Boolean) {
	AppCommanderTheme(
		darkTheme = darkTheme,
	) {
		Confirmation(
			confirmationData = ConfirmationData(show = true, title = Res.string.edit_confirmation_remove),
			onOkSelected = {},
			onDismissRequest = {},
		)
	}
}
