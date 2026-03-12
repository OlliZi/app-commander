package de.joz.appcommander.domain.misc

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveObjectChangesUseCaseTest {
	@Test
	fun `should return false when object was not changed`() =
		runTest {
			val observeObjectChangesUseCase = ObserveObjectChangesUseCase()
			val flow = MutableStateFlow(TestObject(1, "test"))

			backgroundScope.launch {
				observeObjectChangesUseCase.invoke(flow)
			}

			advanceTimeBy(100)

			assertFalse(observeObjectChangesUseCase.wasObjectChanged)
		}

	@Test
	fun `should return true when object was changed`() =
		runTest {
			val observeObjectChangesUseCase = ObserveObjectChangesUseCase()
			val flow = MutableStateFlow(TestObject(1, "test"))

			backgroundScope.launch {
				observeObjectChangesUseCase.invoke(flow)
			}

			advanceTimeBy(100)

			flow.value = TestObject(2, "test")
			advanceTimeBy(100)

			assertTrue(observeObjectChangesUseCase.wasObjectChanged)

			flow.value = TestObject(1, "test")
			advanceTimeBy(100)

			assertFalse(observeObjectChangesUseCase.wasObjectChanged)
		}

	private data class TestObject(
		val value: Int,
		val string: String,
	)
}
