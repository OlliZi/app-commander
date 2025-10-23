package de.joz.appcommander.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationScreens {
	@Serializable
	object WelcomeScreen : NavigationScreens

	@Serializable
	object ScriptsScreen : NavigationScreens

	@Serializable
	data class NewScriptScreen(
		val scriptKey: Int?,
	) : NavigationScreens

	@Serializable
	object SettingsScreen : NavigationScreens
}
