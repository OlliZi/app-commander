package de.joz.app_commander.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationScreens {
    @Serializable
    object WelcomeScreen : NavigationScreens

    @Serializable
    object ScriptsScreen : NavigationScreens

    @Serializable
    object SettingsScreen : NavigationScreens
}