package de.joz.appcommander.domain.logging

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetLoggingUseCase(
    private val loggingRepository: LoggingRepository,
) {
    operator fun invoke(): Flow<List<String>> = loggingRepository.logging
}
