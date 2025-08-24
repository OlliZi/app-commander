package de.joz.app_commander

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.joz.app_commander.domain.NavigationScreens
import de.joz.app_commander.ui.scripts.ScriptsScreen
import de.joz.app_commander.ui.settings.SettingsScreen
import de.joz.app_commander.ui.welcome.WelcomeScreen
import de.joz.app_commander.ui.welcome.WelcomeViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
@Preview
fun App(
    navHostController: NavHostController = rememberNavController()
) {
    MaterialTheme {
        NavHost(
            navController = navHostController,
            startDestination = NavigationScreens.WelcomeScreen,
        ) {
            composable<NavigationScreens.WelcomeScreen> {
                val viewModel: WelcomeViewModel = koinViewModel {
                    parametersOf(navHostController)
                }

                WelcomeScreen(viewModel = viewModel)
            }
            composable<NavigationScreens.ScriptsScreen> {
                ScriptsScreen(executeScriptUseCase = koinInject())
            }
            composable<NavigationScreens.SettingsScreen> {
                SettingsScreen()
            }
        }
    }
}