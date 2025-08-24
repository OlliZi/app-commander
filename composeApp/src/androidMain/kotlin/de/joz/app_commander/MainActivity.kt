package de.joz.app_commander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.koin.compose.KoinApplication
import org.koin.ksp.generated.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            KoinApplication(
                application = {
                    modules(DependencyInjection().module)
                }
            ) {
                App()
            }
        }
    }
}
