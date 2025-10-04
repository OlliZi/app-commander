package de.joz.appcommander.domain.logging

import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class AddLoggingUseCaseTest {
	@Test
	fun `should log to repository`() {
		val loggingRepositoryMock: LoggingRepository = mockk(relaxed = true)

		val useCase =
			AddLoggingUseCase(
				loggingRepository = loggingRepositoryMock,
			)

		useCase(log = "foo")

		verify { loggingRepositoryMock.add(log = "foo") }
	}
}
