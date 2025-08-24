package de.joz.app_commander

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.compose.KoinApplication
import org.koin.ksp.generated.*

fun MainViewController() = ComposeUIViewController {
    KoinApplication(
        application = {
            modules(DependencyInjection().module)
        }
    ) {
        App()
    }
}