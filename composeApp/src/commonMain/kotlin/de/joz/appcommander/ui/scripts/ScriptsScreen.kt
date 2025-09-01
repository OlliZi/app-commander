package de.joz.appcommander.ui.scripts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import compose.icons.FeatherIcons
import compose.icons.feathericons.Settings
import de.joz.appcommander.domain.ExecuteScriptUseCase
import de.joz.appcommander.domain.NavigationScreens
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.scripts_title
import de.joz.appcommander.ui.misc.Action
import de.joz.appcommander.ui.misc.TitleBar
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScriptsScreen(
    executeScriptUseCase: ExecuteScriptUseCase,
    navController: NavController,
) {
    ScriptsContent(navController, executeScriptUseCase)
}

@Composable
private fun ScriptsContent(
    navController: NavController,
    executeScriptUseCase: ExecuteScriptUseCase,
) {
    var log by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize().background(Color.Red),
        topBar = {
            TitleBar(
                title = stringResource(Res.string.scripts_title),
                actions = listOf(
                    Action(
                        action = {
                            // TODO viewmodel
                            navController.navigate(NavigationScreens.SettingsScreen)
                        },
                        icon = FeatherIcons.Settings,
                    )
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val coroutineScope = rememberCoroutineScope()
            Button(onClick = {
                coroutineScope.launch {
                    log =
                        executeScriptUseCase(script = "adb devices", selectedDevice = "").toString()
                }
            }) {
                Text("adb test")
            }

            Text(text = log)
        }
    }
}