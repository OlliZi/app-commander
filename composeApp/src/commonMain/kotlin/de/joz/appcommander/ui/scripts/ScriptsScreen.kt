package de.joz.appcommander.ui.scripts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import compose.icons.feathericons.ArrowRight
import compose.icons.feathericons.ArrowUp
import compose.icons.feathericons.Heart
import compose.icons.feathericons.Settings
import compose.icons.feathericons.Trash
import de.joz.appcommander.domain.ScriptsRepository
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.scripts_hint
import de.joz.appcommander.resources.scripts_hint_devices
import de.joz.appcommander.resources.scripts_hint_no_devices
import de.joz.appcommander.resources.scripts_hint_no_devices_refresh
import de.joz.appcommander.resources.scripts_logging_section_title
import de.joz.appcommander.resources.scripts_open_script_file
import de.joz.appcommander.resources.scripts_terminal_section_title
import de.joz.appcommander.resources.scripts_title
import de.joz.appcommander.ui.misc.Action
import de.joz.appcommander.ui.misc.TitleBar
import de.joz.appcommander.ui.misc.lighter
import de.joz.appcommander.ui.scripts.ScriptsViewModel.Script
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
        },
        onExecuteScript = { script ->
            viewModel.onEvent(event = ScriptsViewModel.Event.OnExecuteScript(script = script))
        },
        onExpand = { script ->
            viewModel.onEvent(event = ScriptsViewModel.Event.OnExpandScript(script = script))
        },
        onOpenScriptFile = {
            viewModel.onEvent(event = ScriptsViewModel.Event.OnOpenScriptFile)
        },
        onClearLogging = {
            viewModel.onEvent(event = ScriptsViewModel.Event.OnClearLogging)
        },
        onExecuteScriptText = {
            viewModel.onEvent(
                event = ScriptsViewModel.Event.OnExecuteScriptText(
                    script = it,
                    platform = ScriptsRepository.Platform.ANDROID
                ),
            )
        },
    )
}

@Composable
internal fun ScriptsContent(
    uiState: ScriptsViewModel.UiState,
    onDeviceSelect: (ScriptsViewModel.Device) -> Unit,
    onRefreshDevices: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onExecuteScript: (Script) -> Unit,
    onExpand: (Script) -> Unit,
    onOpenScriptFile: () -> Unit,
    onClearLogging: () -> Unit,
    onExecuteScriptText: (String) -> Unit,
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
        },
        bottomBar = {
            BottomBar(
                onOpenScriptFile = onOpenScriptFile,
            )
        },
    ) { paddingValues ->
        Column(
            Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val paddingInline = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ConnectedDevices(
                connectedDevices = uiState.connectedDevices,
                modifier = paddingInline,
                onDeviceSelect = onDeviceSelect,
                onRefreshDevices = onRefreshDevices,
            )

            HorizontalDivider()

            ScriptsSection(
                scripts = uiState.scripts,
                isAtMinimumOneDeviceSelected = uiState.connectedDevices.any { it.isSelected },
                modifier = Modifier.weight(1f).then(paddingInline),
                onExecuteScript = onExecuteScript,
                onExpand = onExpand,
            )

            TerminalSection(
                onExecuteScriptText = onExecuteScriptText,
            )

            LoggingSection(
                logging = uiState.logging,
                onClearLogging = onClearLogging,
            )
        }
    }
}

@Composable
private fun ConnectedDevices(
    connectedDevices: List<ScriptsViewModel.Device>,
    onDeviceSelect: (ScriptsViewModel.Device) -> Unit,
    onRefreshDevices: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.scripts_hint),
            style = MaterialTheme.typography.bodySmall,
        )
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = device.label,
                            fontWeight = FontWeight.Bold,
                        )

                        Icon(
                            imageVector = FeatherIcons.Heart,
                            tint = if (device.isSelected) Color.Green.lighter(factor = 0.75f) else Color.LightGray,
                            contentDescription = null,
                        )
                    }
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
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
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
private fun LoggingSection(
    logging: List<String>,
    onClearLogging: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.background(
            Color.LightGray,
        ).padding(8.dp),
    ) {
        Row(
            modifier = Modifier.height(36.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.scripts_logging_section_title),
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            )
            IconButton(
                onClick = onClearLogging,
            ) {
                Icon(
                    imageVector = FeatherIcons.Trash,
                    contentDescription = "Clear logging",
                )
            }
            ExpandButton(
                isExpanded = isExpanded,
                onClick = { isExpanded = !isExpanded },
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            LazyColumn(
                modifier = modifier.heightIn(max = 300.dp).wrapContentHeight(),
            ) {
                items(logging) { item ->
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun TerminalSection(
    onExecuteScriptText: (String) -> Unit,
) {
    var inputValue by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.background(
            Color.LightGray,
        ).padding(8.dp),
    ) {
        Row(
            modifier = Modifier.height(36.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.scripts_terminal_section_title),
                modifier = Modifier.padding(horizontal = 8.dp).weight(1f),
            )
            ExpandButton(
                isExpanded = isExpanded,
                onClick = { isExpanded = !isExpanded },
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    value = inputValue,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                    onValueChange = {
                        inputValue = it
                    },
                    placeholder = {
                        Text(text = "adb devices")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { onExecuteScriptText(inputValue) },
                        ) {
                            Icon(
                                imageVector = FeatherIcons.ArrowRight,
                                contentDescription = "Execute script text",
                            )
                        }
                    },
                )
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
            contentDescription = "Expand button",
        )
    }
}

@Composable
private fun BottomBar(
    onOpenScriptFile: () -> Unit,
) {
    Row(
        modifier = Modifier.navigationBarsPadding().fillMaxWidth()
            .background(Color.LightGray.lighter(factor = 1.1f)).padding(16.dp),
    ) {
        Button(
            onClick = onOpenScriptFile,
        ) {
            Text(
                text = stringResource(Res.string.scripts_open_script_file)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewScriptScreen() {
    ScriptsContent(
        uiState = ScriptsViewModel.UiState(
            connectedDevices = listOf(
                ScriptsViewModel.Device(
                    label = "Pixel 9", isSelected = true, id = "1"
                ), ScriptsViewModel.Device(
                    label = "Pixel 8", isSelected = false, id = "2"
                )
            ), scripts = listOf(
                Script(
                    description = "my script",
                    scriptText = "adb devices",
                    isExpanded = false,
                    originalScript = ScriptsRepository.Script(
                        label = "",
                        script = "",
                        platform = ScriptsRepository.Platform.ANDROID,
                    )
                ), Script(
                    description = "my script",
                    scriptText = "adb long long long long long long long long long long long long long long long  script",
                    isExpanded = true,
                    originalScript = ScriptsRepository.Script(
                        label = "",
                        script = "",
                        platform = ScriptsRepository.Platform.ANDROID,
                    )
                )
            ), logging = listOf("log 1", "log 2", "log 3")
        ),
        onExecuteScript = {},
        onExecuteScriptText = {},
        onRefreshDevices = {},
        onNavigateToSettings = {},
        onExpand = {},
        onOpenScriptFile = {},
        onDeviceSelect = { devive -> },
        onClearLogging = {},
    )
}