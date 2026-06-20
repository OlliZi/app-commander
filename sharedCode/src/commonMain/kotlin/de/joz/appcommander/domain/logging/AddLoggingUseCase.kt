package de.joz.appcommander.domain.logging

import org.koin.core.annotation.Factory

@Factory
class AddLoggingUseCase(
	private val loggingRepository: LoggingRepository,
) {
	operator fun invoke(log: String) {
		loggingRepository.add(log = log)
	}
}
