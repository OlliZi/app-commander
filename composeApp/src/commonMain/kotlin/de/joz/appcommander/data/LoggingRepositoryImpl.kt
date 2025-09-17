package de.joz.appcommander.data

import de.joz.appcommander.domain.logging.LoggingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

@Single
class LoggingRepositoryImpl : LoggingRepository {

    private val _logging = MutableStateFlow(listOf<String>())
    override val logging = _logging.asStateFlow()

    override fun add(log: String) {
        if (log.trim().isEmpty()) {
            return
        }

        _logging.update { oldState ->
            (listOf(log) + oldState).take(MAX_LOGGING_COUNT)
        }
    }

    override fun clear() {
        _logging.update { emptyList() }
    }

    companion object {
        private const val MAX_LOGGING_COUNT = 100
    }
}