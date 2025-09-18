package de.joz.appcommander.ui.welcome

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.joz.appcommander.resources.Res
import de.joz.appcommander.resources.welcome
import de.joz.appcommander.resources.welcome_action
import de.joz.appcommander.resources.welcome_catch_phrase
import de.joz.appcommander.resources.welcome_do_not_show_again
import de.joz.appcommander.resources.welcome_title
import de.joz.appcommander.ui.misc.LabelledSwitch
import de.joz.appcommander.ui.welcome.bubble.BubblesStrategy
import de.joz.appcommander.ui.welcome.bubble.FadingInBubblesStrategy
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    modifier: Modifier = Modifier,
    bubblesStrategy: BubblesStrategy,
    isInTextExecution: Boolean = false,
) {
    WelcomeContent(
        onNavigateToScripts = {
            viewModel.onEvent(event = WelcomeViewModel.Event.OnNavigateToScripts)
        },
        onDoNotShowWelcomeAgain = { checked ->
            viewModel.onEvent(event = WelcomeViewModel.Event.OnDoNotShowWelcomeAgain(value = checked))
        },
        modifier = modifier,
        bubblesStrategy = bubblesStrategy,
        isInTextExecution = isInTextExecution,
    )
}

@Composable
internal fun WelcomeContent(
    bubblesStrategy: BubblesStrategy,
    onNavigateToScripts: () -> Unit,
    onDoNotShowWelcomeAgain: (Boolean) -> Unit,
    isInTextExecution: Boolean,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        val yOffset =
            rememberInfiniteTransition(label = "bubble").animateFloat(
                initialValue = -0.25f,
                targetValue = 1.5f,
                animationSpec =
                    infiniteRepeatable(
                        repeatMode = RepeatMode.Restart,
                        animation =
                            tween(
                                durationMillis = 4000,
                                easing = LinearEasing,
                            ),
                    ),
                label = "bubble",
            ).value

        Column(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    renderBubbles(yOffset, isInTextExecution, bubblesStrategy)
                }
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = stringResource(Res.string.welcome_title),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.welcome_catch_phrase),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            if (!isInTextExecution) { // does not work for screenshot testing
                Image(
                    modifier = Modifier.size(380.dp),
                    painter = painterResource(Res.drawable.welcome),
                    contentDescription = null,
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onNavigateToScripts,
            ) {
                Text(
                    text = stringResource(Res.string.welcome_action),
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
            Spacer(Modifier.weight(1f))

            var isChecked by remember { mutableStateOf(false) }
            LabelledSwitch(
                modifier =
                    Modifier
                        .widthIn(max = 360.dp)
                        .padding(all = 16.dp)
                        .navigationBarsPadding(),
                label = stringResource(Res.string.welcome_do_not_show_again),
                checked = isChecked,
                onCheckedChange = {
                    isChecked = !isChecked
                    onDoNotShowWelcomeAgain(isChecked)
                },
            )
        }
    }
}

private fun DrawScope.renderBubbles(
    yOffset: Float,
    isInTextExecution: Boolean,
    bubblesStrategy: BubblesStrategy,
) {
    if (isInTextExecution.not()) {
        bubblesStrategy.drawBubbles(
            drawScope = this,
            size = size,
            step = yOffset,
        )
    }
}

@Preview
@Composable
private fun PreviewWelcomeScreen() {
    WelcomeContent(
        bubblesStrategy = FadingInBubblesStrategy(),
        onNavigateToScripts = {},
        onDoNotShowWelcomeAgain = {},
        isInTextExecution = false,
    )
}
