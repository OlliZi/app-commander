package de.joz.app_commander

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "App-Commander",
    ) {
        App()
    }
}