package de.joz.appcommander

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.joz.appcommander.domain.NavigationScreens
import de.joz.appcommander.ui.scripts.ScriptsScreen
import de.joz.appcommander.ui.scripts.ScriptsViewModel
import de.joz.appcommander.ui.settings.SettingsScreen
import de.joz.appcommander.ui.settings.SettingsViewModel
import de.joz.appcommander.ui.welcome.WelcomeScreen
import de.joz.appcommander.ui.welcome.WelcomeViewModel
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

                WelcomeScreen(
                    viewModel = viewModel,
                    bubblesStrategy = koinInject(),
                )
            }
            composable<NavigationScreens.ScriptsScreen> {
                val viewModel: ScriptsViewModel = koinViewModel {
                    parametersOf(navHostController)
                }

                ScriptsScreen(
                    viewModel = viewModel,
                )
            }
            composable<NavigationScreens.SettingsScreen> {
                val viewModel: SettingsViewModel = koinViewModel()

                SettingsScreen(
                    viewModel = viewModel,
                    navController = navHostController,
                )
            }
        }
    }
}