package de.joz.app_commander.domain

interface PreferencesRepository {
    suspend fun get(
        key: String,
        defaultValue: Boolean,
    ): Boolean

    suspend fun get(
        key: String,
        defaultValue: Int,
    ): Int

    suspend fun <T> store(
        key: String,
        value: T,
    )
}