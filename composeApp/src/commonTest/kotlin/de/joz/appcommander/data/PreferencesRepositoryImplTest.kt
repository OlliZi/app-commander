package de.joz.appcommander.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PreferencesRepositoryImplTest {
	private val dataStoreMock: DataStore<Preferences> = mockk(relaxed = true)
	private val preferencesRepository = PreferencesRepositoryImpl(
		dataStore = dataStoreMock,
	)

	@Test
	fun `should save int value when store is called`() =
		runTest {
			val lambdaSlot = slot<suspend (Preferences) -> Preferences>()
			coEvery { dataStoreMock.updateData(capture(lambdaSlot)) } returns preferencesOf()

			preferencesRepository.store("foo", 123)

			val mutablePrefs = preferencesOf().toMutablePreferences()
			assertEquals(123, lambdaSlot.captured.invoke(mutablePrefs)[intPreferencesKey("foo")])

			coVerify {
				dataStoreMock.updateData(any())
			}
		}

	@Test
	fun `should save boolean true value when store is called`() =
		runTest {
			val lambdaSlot = slot<suspend (Preferences) -> Preferences>()
			coEvery { dataStoreMock.updateData(capture(lambdaSlot)) } returns preferencesOf()

			preferencesRepository.store("foo", true)

			val mutablePrefs = preferencesOf().toMutablePreferences()
			assertTrue(lambdaSlot.captured.invoke(mutablePrefs)[booleanPreferencesKey("foo")] ?: false)

			coVerify {
				dataStoreMock.updateData(any())
			}
		}

	@Test
	fun `should save boolean false value when store is called`() =
		runTest {
			val lambdaSlot = slot<suspend (Preferences) -> Preferences>()
			coEvery { dataStoreMock.updateData(capture(lambdaSlot)) } returns preferencesOf()

			preferencesRepository.store("foo", false)

			val mutablePrefs = preferencesOf().toMutablePreferences()
			assertFalse(lambdaSlot.captured.invoke(mutablePrefs)[booleanPreferencesKey("foo")] ?: true)

			coVerify {
				dataStoreMock.updateData(any())
			}
		}

	@Test
	fun `should save string value when store is called`() =
		runTest {
			val lambdaSlot = slot<suspend (Preferences) -> Preferences>()
			coEvery { dataStoreMock.updateData(capture(lambdaSlot)) } returns preferencesOf()

			preferencesRepository.store("foo", "test")

			val mutablePrefs = preferencesOf().toMutablePreferences()
			assertEquals("test", lambdaSlot.captured.invoke(mutablePrefs)[stringPreferencesKey("foo")] ?: "")

			coVerify {
				dataStoreMock.updateData(any())
			}
		}
}
