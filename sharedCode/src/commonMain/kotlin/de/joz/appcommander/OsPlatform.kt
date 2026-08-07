package de.joz.appcommander

expect fun getOsPlatform(): OsPlatform

enum class OsPlatform {
	ANDROID,
	IOS,
	DESKTOP,
}
