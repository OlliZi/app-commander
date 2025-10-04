package de.joz.appcommander.domain

import kotlinx.serialization.Serializable

interface ScriptsRepository {
    fun getScripts(): List<Script>

    fun openScriptFile()

    @Serializable
    data class Script(
        val label: String,
        val script: String,
        val platform: Platform,
    )

    enum class Platform(
        val label: String,
    ) {
        ANDROID("Android"),
        IOS("iOS"),
    }
}
