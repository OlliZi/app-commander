package de.joz.app_commander

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello and welcome to your app commander (built for ${platform.name}!"
    }
}