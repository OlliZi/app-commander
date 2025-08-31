package de.joz.appcommander.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test

class PreferencesRepositoryImplTest {
    private val dataStoreMock: DataStore<Preferences> = mockk()
    private val preferencesRepository = PreferencesRepositoryImpl(
        dataStore = dataStoreMock
    )

    @Test
    @Ignore()
    fun `should save value when store is called`() = runTest {
        every { dataStoreMock.data } returns
                flow {
                    emit(preferencesOf(intPreferencesKey("foo") to 123))
                }

        coEvery { dataStoreMock.updateData { mockk() } } returns
                mockk()

        preferencesRepository.store("foo", 123)

        coVerify {
            dataStoreMock.edit { }
        }
    }
}