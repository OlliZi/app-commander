package de.joz.appcommander.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
	suspend fun get(
		key: String,
		defaultValue: Boolean,
	): Boolean

	suspend fun getAsFlow(): Flow<Unit>

	suspend fun get(
		key: String,
		defaultValue: Int,
	): Int

	suspend fun <T> store(
		key: String,
		value: T,
	)
}
