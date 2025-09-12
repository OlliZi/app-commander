package de.joz.appcommander.ui.scripts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.FeatherIcons
import compose.icons.feathericons.Settings
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.scripts_hint_devices
import de.joz.appcommander.resources.scripts_hint_no_devices
import de.joz.appcommander.resources.scripts_hint_no_devices_refresh
import de.joz.appcommander.resources.scripts_title
import de.joz.appcommander.ui.misc.Action
import de.joz.appcommander.ui.misc.TitleBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScriptsScreen(
    viewModel: ScriptsViewModel,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    ScriptsContent(
        uiState = uiState.value,
        onDeviceSelect = { device ->
            viewModel.onEvent(event = ScriptsViewModel.Event.OnDeviceSelected(device = device))
        },
        onRefreshDevices = {
            viewModel.onEvent(event = ScriptsViewModel.Event.OnRefreshDevices)
        },
        onNavigateToSettings = {
            viewModel.onEvent(event = ScriptsViewModel.Event.OnNavigateToSettings)
        }
    )
}

@Composable
private fun ScriptsContent(
    uiState: ScriptsViewModel.UiState,
    onDeviceSelect: (ScriptsViewModel.Device) -> Unit,
    onRefreshDevices: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize().background(Color.Red),
        topBar = {
            TitleBar(
                title = stringResource(Res.string.scripts_title),
                actions = listOf(
                    Action(
                        action = onNavigateToSettings,
                        icon = FeatherIcons.Settings,
                    )
                )
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ConnectedDevices(
                connectedDevices = uiState.connectedDevices,
                onDeviceSelect = onDeviceSelect,
                onRefreshDevices = onRefreshDevices,
            )

            HorizontalDivider()
        }
    }
}

@Composable
private fun ConnectedDevices(
    connectedDevices: List<ScriptsViewModel.Device>,
    onDeviceSelect: (ScriptsViewModel.Device) -> Unit,
    onRefreshDevices: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(if (connectedDevices.isNotEmpty()) Res.string.scripts_hint_devices else Res.string.scripts_hint_no_devices),
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onRefreshDevices,
            ) {
                Text(
                    text = stringResource(Res.string.scripts_hint_no_devices_refresh),
                    fontWeight = FontWeight.Bold,
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
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}