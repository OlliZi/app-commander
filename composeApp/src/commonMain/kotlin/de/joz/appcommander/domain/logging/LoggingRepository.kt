package de.joz.appcommander.domain.logging

import kotlinx.coroutines.flow.Flow

interface LoggingRepository {
    val logging: Flow<List<String>>

    fun add(log: String)

    fun clear()
}
