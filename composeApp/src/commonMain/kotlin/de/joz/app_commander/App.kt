package de.joz.app_commander

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.joz.app_commander.domain.ExecuteScriptUseCase
import de.joz.app_commander.domain.GetPreferenceUseCase
import de.joz.app_commander.domain.NavigationScreens
import de.joz.app_commander.domain.SavePreferenceUseCase
import de.joz.app_commander.ui.scripts.ScriptsScreen
import de.joz.app_commander.ui.settings.SettingsScreen
import de.joz.app_commander.ui.welcome.WelcomeScreen
import de.joz.app_commander.ui.welcome.WelcomeViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    executeScriptUseCase: ExecuteScriptUseCase,
    savePreferenceUseCase: SavePreferenceUseCase,
    getPreferenceUseCase: GetPreferenceUseCase,
) {
    MaterialTheme {
        val navController: NavHostController = rememberNavController()
        val viewModel = WelcomeViewModel(
            savePreferenceUseCase = savePreferenceUseCase,
            getPreferenceUseCase = getPreferenceUseCase,
            navController = navController,
        )

        NavHost(
            navController = navController,
            startDestination = NavigationScreens.WelcomeScreen,
        ) {
            composable<NavigationScreens.WelcomeScreen> {
                WelcomeScreen(viewModel = viewModel)
            }
            composable<NavigationScreens.ScriptsScreen> {
                ScriptsScreen(executeScriptUseCase = executeScriptUseCase)
            }
            composable<NavigationScreens.SettingsScreen> {
                SettingsScreen()
            }
        }
    }
}