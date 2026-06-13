package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.scripts_hint_no_devices_refresh
import de.joz.appcommander.ui.internalpreviews.AppCommanderPreviewParameterProvider
import de.joz.appcommander.ui.internalpreviews.PreviewData
import de.joz.appcommander.ui.internalpreviews.PreviewRenderContainer
import de.joz.appcommander.ui.misc.model.Device
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.stringResource

// move to ConnectedDevices
@Composable
fun DevicesBar(
	connectedDevices: List<Device>,
	onDeviceSelect: (Device) -> Unit,
	onRefreshDevices: () -> Unit,
) {
	FlowRow(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Button(
			onClick = onRefreshDevices,
		) {
			Text(
				text = stringResource(Res.string.scripts_hint_no_devices_refresh),
			)
		}
		connectedDevices.forEach { device ->
			Button(
				modifier = Modifier.alpha(if (device.isSelected) 1f else 0.5f),
				onClick = {
					onDeviceSelect(device)
				},
			) {
				Text(
					text = device.label,
				)
			}
		}
	}
}

@Preview
@Composable
internal fun PreviewDevicesBar() {
	PreviewRenderContainer { previewData ->
		PreviewDevicesBar(previewData)
	}
}

@Preview
@Composable
internal fun PreviewDevicesBar(
	@PreviewParameter(AppCommanderPreviewParameterProvider::class) previewData: PreviewData<Boolean>,
) {
	AppCommanderTheme(
		darkTheme = previewData.uiState,
	) {
		DevicesBar(
			connectedDevices = listOf(
				Device(
					id = "1",
					label = "Device A",
					isSelected = true,
				),
				Device(
					id = "2",
					label = "Device B",
					isSelected = false,
				),
			),
			onDeviceSelect = {},
			onRefreshDevices = {},
		)
	}
}
