package de.joz.appcommander.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ManageUiSAppearanceUseCaseTest {

    @Test
    fun `should retorn default ui appearance when no value was saved`() = runTest {
        val preferencesRepositoryMock: PreferencesRepository = mockk()

        coEvery {
            preferencesRepositoryMock.get(
                key = ManageUiSAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
                defaultValue = ManageUiSAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex,
            )
        } returns -1

        val manageUiSAppearanceUseCase = ManageUiSAppearanceUseCase(
            preferencesRepository = preferencesRepositoryMock,
        )

        assertEquals(
            ManageUiSAppearanceUseCase.UiAppearance.SYSTEM,
            manageUiSAppearanceUseCase.uiAppearanceType.first()
        )
    }

    @Test
    fun `should retorn ui appearance when value was saved`() = runTest {
        val preferencesRepositoryMock: PreferencesRepository = mockk()

        coEvery {
            preferencesRepositoryMock.get(
                key = ManageUiSAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
                defaultValue = ManageUiSAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex,
            )
        } returns ManageUiSAppearanceUseCase.UiAppearance.LIGHT.optionIndex

        val manageUiSAppearanceUseCase = ManageUiSAppearanceUseCase(
            preferencesRepository = preferencesRepositoryMock,
        )

        assertEquals(
            ManageUiSAppearanceUseCase.UiAppearance.LIGHT,
            manageUiSAppearanceUseCase.uiAppearanceType.first()
        )
    }

    @Test
    fun `should save ui appearance when use case is executed`() = runTest {
        val preferencesRepositoryMock: PreferencesRepository = mockk(relaxed = true)

        coEvery {
            preferencesRepositoryMock.get(
                key = ManageUiSAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
                defaultValue = ManageUiSAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex,
            )
        } returns ManageUiSAppearanceUseCase.UiAppearance.LIGHT.optionIndex

        val manageUiSAppearanceUseCase = ManageUiSAppearanceUseCase(
            preferencesRepository = preferencesRepositoryMock,
        )

        manageUiSAppearanceUseCase(uiAppearance = ManageUiSAppearanceUseCase.UiAppearance.DARK)

        coVerify {
            preferencesRepositoryMock.store(
                key = ManageUiSAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
                value = ManageUiSAppearanceUseCase.UiAppearance.DARK.optionIndex
            )
        }
    }
}