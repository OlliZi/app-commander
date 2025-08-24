package de.joz.app_commander.ui.misc

import kotlinx.coroutines.flow.Flow

interface UnidirectionalDataFlow<S, E> {
    val viewState: Flow<S>
    fun onEvent(event: E)
}