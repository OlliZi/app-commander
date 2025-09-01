package de.joz.appcommander.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.settings_preference_show_welcome_screen
import de.joz.appcommander.resources.settings_title
import de.joz.appcommander.ui.misc.SwitchWithLabel
import de.joz.appcommander.ui.misc.TitleBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavController,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        uiState = uiState.value,
        navController = navController,
        onToggleItem = { toggleItem, isChecked ->
            viewModel.onEvent(
                event = SettingsViewModel.Event.OnToggleItem(
                    toggleItem = toggleItem,
                    isChecked = isChecked,
                )
            )
        }
    )
}

@Composable
internal fun SettingsContent(
    uiState: SettingsViewModel.UiState,
    navController: NavController,
    onToggleItem: (SettingsViewModel.ToggleItem, Boolean) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TitleBar(
                title = stringResource(Res.string.settings_title),
                onBackNavigation = {
                    navController.navigateUp()
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
        ) {
            uiState.togglePreferences.forEach { toggleItem ->
                SwitchWithLabel(
                    label = stringResource(Res.string.settings_preference_show_welcome_screen),
                    checked = toggleItem.isChecked,
                    onCheckedChange = { isChecked ->
                        onToggleItem(toggleItem, isChecked)
                    }
                )

            }
        }
    }
}