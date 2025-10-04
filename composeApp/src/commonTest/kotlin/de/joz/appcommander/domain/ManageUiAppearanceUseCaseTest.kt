package de.joz.appcommander.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ManageUiAppearanceUseCaseTest {
	@Test
	fun `should return default ui appearance`() {
		assertEquals(
			ManageUiAppearanceUseCase.UiAppearance.SYSTEM,
			ManageUiAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE,
		)
	}

	@Test
	fun `should return none empty key for STORE_KEY_FOR_SYSTEM_UI_APPEARANCE`() {
		assertEquals(
			"STORE_KEY_FOR_SYSTEM_UI_APPEARANCE",
			ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
		)
	}

	@Test
	fun `should return default ui appearance when no value was saved`() =
		runTest {
			val preferencesRepositoryMock: PreferencesRepository = mockk()

			coEvery {
				preferencesRepositoryMock.get(
					key = ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
					defaultValue = ManageUiAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex,
				)
			} returns -1

			val manageUiAppearanceUseCase =
				ManageUiAppearanceUseCase(
					preferencesRepository = preferencesRepositoryMock,
				)

			assertEquals(
				ManageUiAppearanceUseCase.UiAppearance.SYSTEM,
				manageUiAppearanceUseCase.uiAppearanceType.first(),
			)
		}

	@Test
	fun `should return ui appearance when value was saved`() =
		runTest {
			val preferencesRepositoryMock: PreferencesRepository = mockk()

			coEvery {
				preferencesRepositoryMock.get(
					key = ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
					defaultValue = ManageUiAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex,
				)
			} returns ManageUiAppearanceUseCase.UiAppearance.LIGHT.optionIndex

			val manageUiAppearanceUseCase =
				ManageUiAppearanceUseCase(
					preferencesRepository = preferencesRepositoryMock,
				)

			assertEquals(
				ManageUiAppearanceUseCase.UiAppearance.LIGHT,
				manageUiAppearanceUseCase.uiAppearanceType.first(),
			)
		}

	@Test
	fun `should save ui appearance when use case is executed`() =
		runTest {
			val preferencesRepositoryMock: PreferencesRepository = mockk(relaxed = true)

			coEvery {
				preferencesRepositoryMock.get(
					key = ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
					defaultValue = ManageUiAppearanceUseCase.DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex,
				)
			} returns ManageUiAppearanceUseCase.UiAppearance.LIGHT.optionIndex

			val manageUiAppearanceUseCase =
				ManageUiAppearanceUseCase(
					preferencesRepository = preferencesRepositoryMock,
				)

			manageUiAppearanceUseCase(uiAppearance = ManageUiAppearanceUseCase.UiAppearance.DARK)

			coVerify {
				preferencesRepositoryMock.store(
					key = ManageUiAppearanceUseCase.STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
					value = ManageUiAppearanceUseCase.UiAppearance.DARK.optionIndex,
				)
			}
		}
}
