package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.welcome
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar(
    title: String,
    onBackNavigation: () -> Unit,
    actions: List<Action> = emptyList(),
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(
                onClick = onBackNavigation,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.welcome),
                    contentDescription = null,
                )
            }
        },
        actions = {
            actions.forEach { actionItem ->
                IconButton(
                    onClick = actionItem.action,
                ) {
                    Icon(
                        painter = painterResource(actionItem.icon),
                        contentDescription = null,
                    )
                }
            }
        }
    )
}

data class Action(
    val icon: DrawableResource,
    val action: () -> Unit,
)

@Preview
@Composable
private fun PreviewTitleBar() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            TitleBar(
                title = "Title bar",
                onBackNavigation = {},
                actions = emptyList(),
            )
            TitleBar(
                title = "Title bar with actions",
                onBackNavigation = {},
                actions = listOf(
                    Action(
                        action = {},
                        icon = Res.drawable.welcome,
                    )
                ),
            )
        }
    }
}