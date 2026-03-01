package de.joz.appcommander.domain.preference

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
	suspend fun get(
		key: String,
		defaultValue: Boolean,
	): Boolean

	suspend fun getAsFlow(vararg moreKeys: String): Flow<List<ChangedPreference>>

	suspend fun get(
		key: String,
		defaultValue: Int,
	): Int

	suspend fun <T> store(
		key: String,
		value: T,
	)
}
