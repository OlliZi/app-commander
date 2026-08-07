package de.joz.appcommander

expect fun getOsPlatform(): OsPlatform

fun isDesktop() = getOsPlatform() == OsPlatform.DESKTOP

enum class OsPlatform {
	ANDROID,
	IOS,
	DESKTOP,
}
