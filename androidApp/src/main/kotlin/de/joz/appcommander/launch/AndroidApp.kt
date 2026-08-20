package de.joz.appcommander.launch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.joz.appcommander.App
import de.joz.appcommander.DependencyInjection
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import org.koin.ksp.generated.*

class AndroidApp : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)

		setContent {
			KoinApplication(
				configuration = koinConfiguration(declaration = {
					modules(DependencyInjection().module)
				}),
				content = {
					App()
				},
			)
		}
	}
}
