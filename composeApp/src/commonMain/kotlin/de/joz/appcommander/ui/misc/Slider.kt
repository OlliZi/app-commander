package de.joz.appcommander.ui.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.joz.appcommander.ui.settings.SettingsViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun Slider(
    sliderItem: SettingsViewModel.SliderItem,
    onValueChange: (Float) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(vertical = 12.dp),
    ) {
        Text(
            text = stringResource(sliderItem.label, sliderItem.value.toInt()),
            style = MaterialTheme.typography.bodyLarge,
        )
        Slider(
            value = sliderItem.value,
            steps = sliderItem.steps,
            valueRange = sliderItem.minimum..sliderItem.maximum,
            onValueChange = {
                onValueChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}