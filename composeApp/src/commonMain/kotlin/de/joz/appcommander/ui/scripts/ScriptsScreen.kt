package de.joz.appcommander.ui.scripts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowDown
import compose.icons.feathericons.ArrowUp
import compose.icons.feathericons.Settings
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.scripts_hint_devices
import de.joz.appcommander.resources.scripts_hint_no_devices
import de.joz.appcommander.resources.scripts_hint_no_devices_refresh
import de.joz.appcommander.resources.scripts_title
import de.joz.appcommander.ui.misc.Action
import de.joz.appcommander.ui.misc.TitleBar
import de.joz.appcommander.ui.scripts.ScriptsViewModel.Script
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScriptsScreen(
    viewModel: ScriptsViewModel,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    ScriptsContent(uiState = uiState.value, onDeviceSelect = { device ->
        viewModel.onEvent(event = ScriptsViewModel.Event.OnDeviceSelected(device = device))
    }, onRefreshDevices = {
        viewModel.onEvent(event = ScriptsViewModel.Event.OnRefreshDevices)
    }, onNavigateToSettings = {
        viewModel.onEvent(event = ScriptsViewModel.Event.OnNavigateToSettings)
    }, onExecuteScript = { script ->
        viewModel.onEvent(event = ScriptsViewModel.Event.OnExecuteScript(script = script))
    }, onExpand = { script ->
        viewModel.onEvent(event = ScriptsViewModel.Event.OnExpandScript(script = script))
    })
}

@Composable
internal fun ScriptsContent(
    uiState: ScriptsViewModel.UiState,
    onDeviceSelect: (ScriptsViewModel.Device) -> Unit,
    onRefreshDevices: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onExecuteScript: (Script) -> Unit,
    onExpand: (Script) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize().background(Color.Red),
        topBar = {
            TitleBar(
                title = stringResource(Res.string.scripts_title), actions = listOf(
                    Action(
                        action = onNavigateToSettings,
                        icon = FeatherIcons.Settings,
                    )
                )
            )
        }) { paddingValues ->
        Column(
            Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ConnectedDevices(
                connectedDevices = uiState.connectedDevices,
                onDeviceSelect = onDeviceSelect,
                onRefreshDevices = onRefreshDevices,
            )

            HorizontalDivider()

            ScriptsSection(
                scripts = uiState.scripts,
                isAtMinimumOneDeviceSelected = uiState.connectedDevices.any { it.isSelected },
                onExecuteScript = onExecuteScript,
                onExpand = onExpand,
            )
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

@Composable
private fun ScriptsSection(
    scripts: List<Script>,
    isAtMinimumOneDeviceSelected: Boolean,
    onExecuteScript: (Script) -> Unit,
    onExpand: (Script) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(scripts) { script ->
            Button(
                enabled = isAtMinimumOneDeviceSelected,
                shape = RoundedCornerShape(10.dp),
                onClick = { onExecuteScript(script) },
            ) {
                if (script.isExpanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = script.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = script.scriptText,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        ExpandButton(
                            isExpanded = true,
                            onClick = { onExpand(script) },
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = script.description,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = " | ",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            text = script.scriptText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        ExpandButton(
                            isExpanded = false,
                            onClick = { onExpand(script) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandButton(
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            imageVector = if (isExpanded) FeatherIcons.ArrowUp else FeatherIcons.ArrowDown,
            contentDescription = null,
        )
    }
}