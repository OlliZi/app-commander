package de.joz.appcommander.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoggingRepositoryImplTest {

    @Test
    fun `should empty after startup`() = runTest {
        val repository = createRepository()

        assertTrue(repository.logging.value.isEmpty())
    }

    @Test
    fun `should add log`() = runTest {
        val repository = createRepository()

        repository.add("foo")

        assertEquals(listOf("foo"), repository.logging.value)
    }

    @Test
    fun `should clear log`() = runTest {
        val repository = createRepository()

        repository.add("foo")
        repository.clear()

        assertTrue(repository.logging.value.isEmpty())
    }

    @Test
    fun `should trunk log`() = runTest {
        val repository = createRepository()

        (0..1000).forEach {
            repository.add(it.toString())
        }

        assertTrue(repository.logging.value.size == 100)
    }

    private fun createRepository() = LoggingRepositoryImpl()
}