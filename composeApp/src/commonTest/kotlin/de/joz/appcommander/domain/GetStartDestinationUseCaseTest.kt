package de.joz.appcommander.domain

import de.joz.appcommander.ui.settings.SettingsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class GetStartDestinationUseCaseTest {
    @Test
    fun `should navigate to welcome screen when nothing is saved in preferences`() =
        runTest {
            val getPreferenceUseCaseMock: GetPreferenceUseCase = mockk()
            coEvery {
                getPreferenceUseCaseMock.get(
                    key = any<String>(),
                    defaultValue = any<Boolean>(),
                )
            } returns false

            val getStartDestination =
                GetStartDestinationUseCase(
                    getPreferenceUseCase = getPreferenceUseCaseMock,
                )

            assertEquals(NavigationScreens.WelcomeScreen, getStartDestination(TestScope()))

            coVerify {
                getPreferenceUseCaseMock.get(
                    key = SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY,
                    defaultValue = false,
                )
            }
        }

    @Test
    fun `should navigate to scripts screen when flag is saved in preferences`() =
        runTest {
            val getPreferenceUseCaseMock: GetPreferenceUseCase = mockk()
            coEvery {
                getPreferenceUseCaseMock.get(
                    key = any<String>(),
                    defaultValue = any<Boolean>(),
                )
            } returns true

            val getStartDestination =
                GetStartDestinationUseCase(
                    getPreferenceUseCase = getPreferenceUseCaseMock,
                )

            assertEquals(NavigationScreens.ScriptsScreen, getStartDestination(TestScope()))

            coVerify {
                getPreferenceUseCaseMock.get(
                    key = SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY,
                    defaultValue = false,
                )
            }
        }
}
