package de.joz.appcommander.data

import de.joz.appcommander.helper.TestRuleApplier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoggingRepositoryImplTest : TestRuleApplier() {
	@Test
	fun `should empty after startup`() {
		val repository = createRepository()

		assertTrue(repository.logging.value.isEmpty())
	}

	@Test
	fun `should add log`() {
		val repository = createRepository()

		repository.add("foo")

		assertEquals(listOf("foo"), repository.logging.value)
	}

	@Test
	fun `should ignore empty log`() {
		val repository = createRepository()

		repository.add("")

		assertTrue(repository.logging.value.isEmpty())
	}

	@Test
	fun `should clear log`() {
		val repository = createRepository()

		repository.add("foo")
		repository.clear()

		assertTrue(repository.logging.value.isEmpty())
	}

	@Test
	fun `should trunk log`() {
		val repository = createRepository()

		(0..1000).forEach {
			repository.add(it.toString())
		}

		assertEquals(100, repository.logging.value.size)
	}

	private fun createRepository() = LoggingRepositoryImpl()
}
