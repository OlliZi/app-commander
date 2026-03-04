package de.joz.appcommander.domain.logging

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class GetLoggingUseCaseTest {
	@Test
	fun `should get logs from repository`() =
		runTest {
			val loggingRepositoryMock: LoggingRepository = mockk(relaxed = true)
			every { loggingRepositoryMock.logging } returns flowOf(listOf("foo", "bar"))

			val useCase =
				GetLoggingUseCase(
					loggingRepository = loggingRepositoryMock,
				)

			assertTrue(useCase().first().isNotEmpty())
		}
}
