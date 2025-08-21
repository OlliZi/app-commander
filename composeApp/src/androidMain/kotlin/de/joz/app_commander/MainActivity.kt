package de.joz.app_commander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.joz.app_commander.domain.ExecuteScriptUseCase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val executeScriptUseCase = ExecuteScriptUseCase()

        setContent {
            App(
                executeScriptUseCase = executeScriptUseCase,
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val executeScriptUseCase = ExecuteScriptUseCase()

    App(
        executeScriptUseCase = executeScriptUseCase,
    )
}