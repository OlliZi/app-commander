package de.joz.appcommander.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

@Single
class ManageUiModeUseCase(
    private val preferencesRepository: PreferencesRepository,
) {
    private val _uiModeType = MutableStateFlow<UiMode>(DEFAULT_SYSTEM_UI_MODE)
    val uiModeType: Flow<UiMode> = _uiModeType.onStart {
        updateUiModeState()
    }

    suspend operator fun invoke(uiMode: UiMode) {
        preferencesRepository.store(STORE_KEY_FOR_SYSTEM_UI_APPEARANCE, uiMode.optionIndex)
        updateUiModeState()
    }

    private suspend fun updateUiModeState() {
        _uiModeType.update { oldState ->
            val savedUiMode = preferencesRepository.get(
                key = STORE_KEY_FOR_SYSTEM_UI_APPEARANCE,
                defaultValue = DEFAULT_SYSTEM_UI_MODE.optionIndex
            )
            UiMode.entries.find { it.optionIndex == savedUiMode } ?: DEFAULT_SYSTEM_UI_MODE
        }
    }

    enum class UiMode(val optionIndex: Int) {
        SYSTEM_MODE(optionIndex = 0),
        DARK_MODE(optionIndex = 1),
        LIGHT_MODE(optionIndex = 2),
    }

    companion object Companion {
        const val STORE_KEY_FOR_SYSTEM_UI_APPEARANCE = "STORE_KEY_FOR_SYSTEM_UI_APPEARANCE"
        val DEFAULT_SYSTEM_UI_MODE = UiMode.SYSTEM_MODE
    }
}
