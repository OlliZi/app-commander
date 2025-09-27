package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowDown
import compose.icons.feathericons.ArrowUp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ExpandButton(
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            imageVector = if (isExpanded) FeatherIcons.ArrowUp else FeatherIcons.ArrowDown,
            contentDescription = "Expand button",
        )
    }
}

@Preview
@Composable
private fun PreviewExpandButton() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            ExpandButton(
                isExpanded = true,
                onClick = {},
            )
            ExpandButton(
                isExpanded = false,
                onClick = {},
            )
        }
    }
}