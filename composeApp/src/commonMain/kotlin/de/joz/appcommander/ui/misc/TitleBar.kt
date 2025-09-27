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
import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Settings
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar(
    title: String,
    onBackNavigation: (() -> Unit)? = null,
    actions: List<Action> = emptyList(),
) {
    TopAppBar(title = {
        Text(text = title)
    }, navigationIcon = {
        if (onBackNavigation != null) {
            IconButton(
                onClick = onBackNavigation,
            ) {
                Icon(
                    imageVector = FeatherIcons.ArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }, actions = {
        actions.forEach { actionItem ->
            IconButton(
                onClick = actionItem.action,
            ) {
                Icon(
                    imageVector = actionItem.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    })
}

data class Action(
    val icon: ImageVector,
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
                title = "Title bar (plain)",
            )
            TitleBar(
                title = "Title bar with back",
                onBackNavigation = {},
            )
            TitleBar(
                title = "Title bar with back + actions",
                onBackNavigation = {},
                actions = listOf(
                    Action(
                        action = {},
                        icon = FeatherIcons.Settings,
                    )
                ),
            )
        }
    }
}