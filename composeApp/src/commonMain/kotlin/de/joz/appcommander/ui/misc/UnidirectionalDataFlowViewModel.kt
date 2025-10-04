package de.joz.appcommander.ui.misc

import kotlinx.coroutines.flow.Flow

interface UnidirectionalDataFlowViewModel<S, E> {
    val uiState: Flow<S>

    fun onEvent(event: E)
}
