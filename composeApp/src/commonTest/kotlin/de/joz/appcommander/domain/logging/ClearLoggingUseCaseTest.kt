package de.joz.appcommander.domain.logging

import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class ClearLoggingUseCaseTest {
    @Test
    fun `should clear logs`() {
        val loggingRepositoryMock: LoggingRepository = mockk(relaxed = true)

        val useCase =
            ClearLoggingUseCase(
                loggingRepository = loggingRepositoryMock,
            )

        useCase()

        verify { loggingRepositoryMock.clear() }
    }
}
