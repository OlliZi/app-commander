package de.joz.appcommander

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.joz.appcommander.domain.GetStartDestinationUseCase
import de.joz.appcommander.domain.ManageUiAppearanceUseCase
import de.joz.appcommander.domain.NavigationScreens
import de.joz.appcommander.ui.scripts.ScriptsScreen
import de.joz.appcommander.ui.scripts.ScriptsViewModel
import de.joz.appcommander.ui.settings.SettingsScreen
import de.joz.appcommander.ui.settings.SettingsViewModel
import de.joz.appcommander.ui.theme.AppCommanderTheme
import de.joz.appcommander.ui.welcome.WelcomeScreen
import de.joz.appcommander.ui.welcome.WelcomeViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App(
    navHostController: NavHostController = rememberNavController(),
    getStartDestination: GetStartDestinationUseCase = koinInject(),
    manageUiAppearanceUseCase: ManageUiAppearanceUseCase = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()
    val uiAppearanceType =
        manageUiAppearanceUseCase.uiAppearanceType.collectAsStateWithLifecycle(
            initialValue = ManageUiAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE,
        )

    AppCommanderTheme(
        darkTheme = isDarkThemeEnabled(uiAppearanceType.value),
    ) {
        NavHost(
            navController = navHostController,
            startDestination = getStartDestination(coroutineScope),
        ) {
            composable<NavigationScreens.WelcomeScreen> {
                val viewModel: WelcomeViewModel =
                    koinViewModel {
                        parametersOf(navHostController)
                    }

                WelcomeScreen(
                    viewModel = viewModel,
                    bubblesStrategy = koinInject(),
                )
            }
            composable<NavigationScreens.ScriptsScreen> {
                val viewModel: ScriptsViewModel =
                    koinViewModel {
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

@Composable
@ReadOnlyComposable
private fun isDarkThemeEnabled(uiAppearance: ManageUiAppearanceUseCase.UiAppearance): Boolean =
    when (uiAppearance) {
        ManageUiAppearanceUseCase.UiAppearance.DARK -> true
        ManageUiAppearanceUseCase.UiAppearance.LIGHT -> false
        ManageUiAppearanceUseCase.UiAppearance.SYSTEM -> isSystemInDarkTheme()
    }
