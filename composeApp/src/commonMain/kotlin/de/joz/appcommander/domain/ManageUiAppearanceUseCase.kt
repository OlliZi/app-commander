package de.joz.appcommander.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

@Single
class ManageUiAppearanceUseCase(
	private val preferencesRepository: PreferencesRepository,
) {
	private val _uiAppearanceType = MutableStateFlow<UiAppearance>(DEFAULT_SYSTEM_UI_APPEARANCE)
	val uiAppearanceType: Flow<UiAppearance> =
		_uiAppearanceType.onStart {
			updateUiAppearance()
		}

	suspend operator fun invoke(uiAppearance: UiAppearance) {
		preferencesRepository.store(STORE_KEY_FOR_SYSTEM_UI_APPEARANCE, uiAppearance.optionIndex)
		updateUiAppearance()
	}

	private suspend fun updateUiAppearance() {
		_uiAppearanceType.update { oldState ->
			val savedUiAppearance =
				preferencesRepository.get(
					key = STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
					defaultValue = DEFAULT_SYSTEM_UI_APPEARANCE.optionIndex,
				)
			UiAppearance.entries.find { it.optionIndex == savedUiAppearance }
				?: DEFAULT_SYSTEM_UI_APPEARANCE
		}
	}

	enum class UiAppearance(
		val optionIndex: Int,
	) {
		SYSTEM(optionIndex = 0),
		DARK(optionIndex = 1),
		LIGHT(optionIndex = 2),
	}

	companion object Companion {
		const val STORE_KEY_FOR_SYSTEM_UI_APPEARANCE = "STORE_KEY_FOR_SYSTEM_UI_APPEARANCE"
		val DEFAULT_SYSTEM_UI_APPEARANCE = UiAppearance.SYSTEM
	}
}
