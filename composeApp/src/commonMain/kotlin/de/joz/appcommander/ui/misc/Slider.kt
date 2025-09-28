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
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.settings_preference_track_scripts_file_delay_slider_label
import de.joz.appcommander.ui.settings.SettingsViewModel
import de.joz.appcommander.ui.theme.AppCommanderTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
            text = stringResource(sliderItem.label, sliderItem.labelValue(sliderItem.sliderValue)),
            style = MaterialTheme.typography.bodyLarge,
        )
        Slider(
            value = sliderItem.sliderValue,
            steps = sliderItem.steps,
            valueRange = sliderItem.minimum..sliderItem.maximum,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun PreviewSlider_Dark() {
    AppCommanderTheme(
        darkTheme = true
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Slider(
                sliderItem = SettingsViewModel.SliderItem(
                    sliderValue = 5f,
                    maximum = 10f,
                    minimum = 0f,
                    steps = 20,
                    label = Res.string.settings_preference_track_scripts_file_delay_slider_label,
                    key = "",
                ),
                onValueChange = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSlider_Light() {
    AppCommanderTheme(
        darkTheme = false
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Slider(
                sliderItem = SettingsViewModel.SliderItem(
                    sliderValue = 5f,
                    maximum = 10f,
                    minimum = 0f,
                    steps = 20,
                    label = Res.string.settings_preference_track_scripts_file_delay_slider_label,
                    key = "",
                ),
                onValueChange = {},
            )
        }
    }
}