package de.joz.appcommander.domain

interface ScriptsRepository {

    fun getScripts(): List<Script>

    data class Script(
        val label: String,
        val script: String,
        val platform: Platform,
    )

    enum class Platform {
        ANDROID,
        IOS,
    }
}