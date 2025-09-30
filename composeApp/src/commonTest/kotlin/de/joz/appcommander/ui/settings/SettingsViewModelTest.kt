package de.joz.appcommander.ui.settings

import de.joz.appcommander.domain.GetPreferenceUseCase
import de.joz.appcommander.domain.ManageUiSAppearanceUseCase
import de.joz.appcommander.domain.SavePreferenceUseCase
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.settings_preference_show_welcome_screen
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val savePreferenceUseCaseMock: SavePreferenceUseCase = mockk(relaxed = true)
    private val getPreferenceUseCaseMock: GetPreferenceUseCase = mockk()
    private val manageUiSAppearanceUseCaseMock: ManageUiSAppearanceUseCase = mockk(relaxed = true)

    @Test
    fun `should return default state when viewmodel is initialized`() = runTest {
        coEvery {
            getPreferenceUseCaseMock.get(any<String>(), any<Boolean>())
        } returns false

        coEvery {
            getPreferenceUseCaseMock.get(any<String>(), any<Int>())
        } returns 0

        val viewModel = createViewModel()
        runCurrent()

        val uiState = viewModel.uiState.value

        assertEquals(1, uiState.togglePreferences.size)
        assertEquals(2, uiState.sliderPreferences.size)

        assertEquals(
            SettingsViewModel.ToggleItem(
                isChecked = false,
                label = Res.string.settings_preference_show_welcome_screen,
                key = SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY,
            ),
            uiState.togglePreferences.first(),
        )
    }

    private fun createViewModel(): SettingsViewModel {
        return SettingsViewModel(
            savePreferenceUseCase = savePreferenceUseCaseMock,
            getPreferenceUseCase = getPreferenceUseCaseMock,
            manageUiSAppearanceUseCase = manageUiSAppearanceUseCaseMock,
            dispatcher = Dispatchers.Unconfined,
        )
    }
}