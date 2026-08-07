package de.joz.appcommander.domain.platform

expect fun getOsPlatform(): OsPlatform

enum class OsPlatform {
	ANDROID,
	IOS,
	DESKTOP,
}
