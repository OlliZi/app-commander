package de.joz.appcommander.ui.settings

import de.joz.appcommander.domain.GetPreferenceUseCase
import de.joz.appcommander.domain.ManageUiSAppearanceUseCase
import de.joz.appcommander.domain.SavePreferenceUseCase
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.settings_preference_show_welcome_screen
import de.joz.appcommander.resources.settings_preference_track_scripts_file_delay_slider_label
import de.joz.appcommander.resources.settings_preference_ui_appearance_label
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
    fun `should return none empty key for HIDE_WELCOME_SCREEN_PREF_KEY`() {
        assertEquals(
            "HIDE_WELCOME_SCREEN",
            SettingsViewModel.HIDE_WELCOME_SCREEN_PREF_KEY,
        )
    }

    @Test
    fun `should return none empty key for TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY`() {
        assertEquals(
            "TRACK_SCRIPTS_FILE_DELAY_SLIDER",
            SettingsViewModel.TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY,
        )
    }

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

        assertEquals(
            SettingsViewModel.SliderItem(
                maximum = 10f,
                minimum = 1f,
                steps = 8,
                sliderValue = 0f,
                label = Res.string.settings_preference_track_scripts_file_delay_slider_label,
                key = SettingsViewModel.TRACK_SCRIPTS_FILE_DELAY_SLIDER_PREF_KEY,
            ),
            uiState.sliderPreferences[0],
        )

        assertEquals(
            SettingsViewModel.SliderItem(
                maximum = 2f,
                minimum = 0f,
                steps = 1,
                sliderValue = 0f,
                label = Res.string.settings_preference_ui_appearance_label,
                key = ManageUiSAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
            ),
            uiState.sliderPreferences[1],
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