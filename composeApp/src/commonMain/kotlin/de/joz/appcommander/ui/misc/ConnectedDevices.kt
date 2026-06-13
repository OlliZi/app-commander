package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.scripts_hint
import de.joz.appcommander.resources.scripts_hint_devices
import de.joz.appcommander.resources.scripts_hint_no_devices
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.internalpreviews.PreviewRenderContainer
import de.joz.appcommander.ui.misc.model.Device
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConnectedDevices(
	connectedDevices: List<Device>,
	onDeviceSelect: (Device) -> Unit,
	onRefreshDevices: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		TextLabel(
			text = stringResource(
				if (connectedDevices.isNotEmpty()) {
					Res.string.scripts_hint_devices
				} else {
					Res.string.scripts_hint_no_devices
				},
			),
			textLabelType = TextLabelType.BodyLarge,
		)

		DevicesBar(
			connectedDevices = connectedDevices,
			onDeviceSelect = onDeviceSelect,
			onRefreshDevices = onRefreshDevices,
		)

		TextLabel(
			text = stringResource(Res.string.scripts_hint),
			textLabelType = TextLabelType.BodySmall,
		)
	}
}

@Preview
@Composable
internal fun PreviewConnectedDevices() {
	PreviewRenderContainer { previewData ->
		PreviewConnectedDevices(previewData)
	}
}

@Preview
@Composable
internal fun PreviewConnectedDevices(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = previewData.uiState,
	) {
		ConnectedDevices(
			connectedDevices = listOf(
				Device(
					id = "1",
					label = "Pixel 10",
					isSelected = true,
				),
				Device(
					id = "2",
					label = "Pixel 8",
					isSelected = false,
				),
				Device(
					id = "3",
					label = "Pixel 7",
					isSelected = true,
				),
			),
			onDeviceSelect = {},
			onRefreshDevices = {},
		)
	}
}
