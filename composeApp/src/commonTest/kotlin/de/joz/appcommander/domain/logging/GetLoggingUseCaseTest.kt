package de.joz.appcommander.domain.logging

import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetLoggingUseCaseTest {
	@Test
	fun `should get logs from repository`() =
		runTest {
			val loggingRepositoryMock: LoggingRepository = mockk(relaxed = true)

			val useCase =
				GetLoggingUseCase(
					loggingRepository = loggingRepositoryMock,
				)

			useCase().collect { }

			verify { loggingRepositoryMock.logging }
		}
}
