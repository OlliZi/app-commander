package de.joz.appcommander.domain.logging

import org.koin.core.annotation.Factory

@Factory
class ClearLoggingUseCase(
    private val loggingRepository: LoggingRepository,
) {
    operator fun invoke() {
        loggingRepository.clear()
    }
}