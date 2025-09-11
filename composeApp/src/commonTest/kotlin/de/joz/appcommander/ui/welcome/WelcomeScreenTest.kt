package de.joz.appcommander.ui.welcome

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import de.joz.appcommander.DependencyInjection
import de.joz.appcommander.domain.PreferencesRepository
import de.joz.appcommander.helper.PreferencesRepositoryMock
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ksp.generated.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class WelcomeScreenTest {
    private lateinit var koin: Koin

    private val preferencesRepositoryMock = PreferencesRepositoryMock()

    @BeforeTest
    fun setup() {
        koin = startKoin {
            modules(
                DependencyInjection().module + module {
                    single<PreferencesRepository> { preferencesRepositoryMock }
                }
            )
        }.koin
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should display all default labels on screen`() {
        runComposeUiTest {
            setContent {
                WelcomeScreen(
                    viewModel = WelcomeViewModel(
                        navController = rememberNavController(),
                        savePreferenceUseCase = koin.get(),
                        getPreferenceUseCase = koin.get(),
                    ),
                    bubblesStrategy = koin.get(),
                )
            }

            onNodeWithText("Welcome to \n'App-Commander'.").assertIsDisplayed()
            onNodeWithText("Execute your custom scripts for your apps on multiple devices.").assertIsDisplayed()
            onNodeWithText("Let`s go!").assertIsDisplayed().assertHasClickAction()
            onNodeWithText("Do not show welcome screen again.").assertIsDisplayed()
        }
    }

    @Test
    fun `should save flag when toggle is clicked`() = runTest {
        runComposeUiTest {
            setContent {
                WelcomeScreen(
                    viewModel = WelcomeViewModel(
                        navController = rememberNavController(),
                        savePreferenceUseCase = koin.get(),
                        getPreferenceUseCase = koin.get(),
                    ),
                    bubblesStrategy = koin.get(),
                )
            }

            onNodeWithText("Do not show welcome screen again.").performClick()
        }

        assertTrue(
            preferencesRepositoryMock.get(
                "HIDE_WELCOME_SCREEN_PREF_KEY",
                false,
            )
        )
    }

    @Test
    fun `should revert toggle value when toggle is clicked twice`() = runTest {
        runComposeUiTest {
            setContent {
                WelcomeScreen(
                    viewModel = WelcomeViewModel(
                        navController = rememberNavController(),
                        savePreferenceUseCase = koin.get(),
                        getPreferenceUseCase = koin.get(),
                    ),
                    bubblesStrategy = koin.get(),
                )
            }

            onNodeWithText("Do not show welcome screen again.").performClick()
            onNodeWithText("Do not show welcome screen again.").performClick()
        }

        assertFalse(
            preferencesRepositoryMock.get(
                "HIDE_WELCOME_SCREEN_PREF_KEY",
                true,
            )
        )
    }

    @Test
    @Ignore
    fun `should navigate to next screen when next button is clicked`() {
        val navController: NavController = mockk()
        runComposeUiTest {
            setContent {
                WelcomeScreen(
                    viewModel = WelcomeViewModel(
                        navController = navController,
                        savePreferenceUseCase = koin.get(),
                        getPreferenceUseCase = koin.get(),
                    ),
                    bubblesStrategy = koin.get(),
                )

            }

            onNodeWithText("Los geht's!").performClick()
            waitForIdle()

            waitUntilAtLeastOneExists(hasText("adb test"))
        }
    }
}