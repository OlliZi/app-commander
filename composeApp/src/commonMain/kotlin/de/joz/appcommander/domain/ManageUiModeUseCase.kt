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
        preferencesRepository.store(STORE_KEY_FOR_SYSTEM_UI_MODE, uiMode.ordinal)
        updateUiModeState()
    }

    private suspend fun updateUiModeState() {
        _uiModeType.update { oldState ->
            val savedUiMode = preferencesRepository.get(
                key = STORE_KEY_FOR_SYSTEM_UI_MODE,
                defaultValue = DEFAULT_SYSTEM_UI_MODE.ordinal
            )
            UiMode.entries.find { it.ordinal == savedUiMode } ?: DEFAULT_SYSTEM_UI_MODE
        }
    }

    enum class UiMode {
        DARK_MODE,
        LIGHT_MODE,
        SYSTEM_MODE,
    }

    companion object Companion {
        private const val STORE_KEY_FOR_SYSTEM_UI_MODE = "STORE_KEY_FOR_SYSTEM_UI_MODE"
        val DEFAULT_SYSTEM_UI_MODE = UiMode.DARK_MODE
    }
}
