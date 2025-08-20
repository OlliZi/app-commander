package de.joz.app_commander

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform