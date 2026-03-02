package de.joz.appcommander.domain.preference

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
	suspend fun get(
		key: String,
		defaultValue: Boolean,
	): Boolean

	suspend fun get(
		key: String,
		defaultValue: Int,
	): Int

	suspend fun get(
		key: String,
		defaultValue: String,
	): String

	suspend fun <T> store(
		key: String,
		value: T,
	)

	suspend fun getAsFlow(vararg keys: String): Flow<List<ChangedPreference>>
}
