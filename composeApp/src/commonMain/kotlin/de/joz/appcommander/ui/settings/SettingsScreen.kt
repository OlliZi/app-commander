package de.joz.appcommander.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.settings_preference_show_welcome_screen
import de.joz.appcommander.resources.settings_preference_track_scripts_file_delay_slider_label
import de.joz.appcommander.resources.settings_title
import de.joz.appcommander.ui.misc.LabelledSwitch
import de.joz.appcommander.ui.misc.SectionDivider
import de.joz.appcommander.ui.misc.Slider
import de.joz.appcommander.ui.misc.TitleBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
	val uiState =
		viewModel.uiState.collectAsStateWithLifecycle()

	SettingsScreen(
		viewModel = viewModel,
		uiState = uiState.value,
	)
}

@Composable
internal fun SettingsScreen(
	viewModel: SettingsViewModel,
	uiState: SettingsViewModel.UiState,
) {
	SettingsContent(
		uiState = uiState,
		onBackNavigation = {
			viewModel.onEvent(event = SettingsViewModel.Event.OnNavigateBack)
		},
		onToggleItem = { toggleItem, isChecked ->
			viewModel.onEvent(
				event =
					SettingsViewModel.Event.OnToggleItem(
						toggleItem = toggleItem,
						isChecked = isChecked,
					),
			)
		},
		onSliderChangeItem = { sliderItem, value ->
			viewModel.onEvent(
				event =
					SettingsViewModel.Event.OnSliderItem(
						sliderItem = sliderItem,
						value = value,
					),
			)
		},
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsContent(
	uiState: SettingsViewModel.UiState,
	onBackNavigation: () -> Unit,
	onToggleItem: (SettingsViewModel.ToggleItem, Boolean) -> Unit,
	onSliderChangeItem: (SettingsViewModel.SliderItem, Float) -> Unit,
) {
	Scaffold(
		containerColor = MaterialTheme.colorScheme.surface,
		topBar = {
			TitleBar(
				title = stringResource(Res.string.settings_title),
				onBackNavigation = onBackNavigation,
			)
		},
	) { paddingValues ->
		Column(
			Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(16.dp)
				.verticalScroll(rememberScrollState()),
			verticalArrangement = Arrangement.Top,
		) {
			uiState.togglePreferences.forEach { toggleItem ->
				LabelledSwitch(
					textModifier = Modifier.weight(1f),
					label = stringResource(toggleItem.label),
					checked = toggleItem.isChecked,
					onCheckedChange = { isChecked ->
						onToggleItem(toggleItem, isChecked)
					},
				)
			}

			SectionDivider()

			uiState.sliderPreferences.forEach { sliderItem ->
				Slider(
					sliderItem = sliderItem,
					onValueChange = { value ->
						onSliderChangeItem(sliderItem, value)
					},
				)
			}
		}
	}
}

@Preview
@Composable
private fun PreviewSettingsScreen() {
	SettingsContent(
		uiState =
			SettingsViewModel.UiState(
				togglePreferences =
					listOf(
						SettingsViewModel.ToggleItem(
							isChecked = true,
							label = Res.string.settings_preference_show_welcome_screen,
							key = "",
						),
					),
				sliderPreferences =
					listOf(
						SettingsViewModel.SliderItem(
							label = Res.string.settings_preference_track_scripts_file_delay_slider_label,
							sliderValue = 4f,
							key = "",
							minimum = 0f,
							maximum = 10f,
							steps = 10,
							labelValue = SettingsViewModel.LabelValue.IntRes(5),
						),
					),
			),
		onSliderChangeItem = { _, _ -> },
		onToggleItem = { _, _ -> },
		onBackNavigation = {},
	)
}
