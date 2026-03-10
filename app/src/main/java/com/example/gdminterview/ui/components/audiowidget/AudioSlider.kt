package com.example.gdminterview.ui.components.audiowidget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioSlider(
    progressMs: Long,
    durationMs: Long,
    bufferedFraction: Float,
    progressFraction: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Slider(
        // Make it so track edges is flush with the container, and thumb juts out past
        // the container.
        // TODO: does UX actually want this? UX spec doesn't show thumb-at-start or
        //   thumb-at-end cases.
        modifier = modifier
            .semantics {
                contentDescription = "Song progress, " +
                    formatDuration(progressMs) +
                    " of " +
                    formatDuration(durationMs)
            }
            .layout { measurable, constraints ->
            val thumbHalfWidth = 6.dp.roundToPx() // half of 12dp thumb
            val placeable = measurable.measure(
                constraints.copy(maxWidth = constraints.maxWidth + thumbHalfWidth * 2)
            )
            layout(constraints.maxWidth, placeable.height) {
                placeable.place(-thumbHalfWidth, 0)
            }
        },
        value = progressMs.toFloat(),
        onValueChange = onProgressChange,
        valueRange = 0f..durationMs.toFloat(),
        track = {
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                // Track
                Box(
                    Modifier
                        .fillMaxWidth()
                        // TODO: verify dimension with UX
                        .height(2.404.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                // Buffered
                Box(
                    Modifier
                        .fillMaxWidth(fraction = bufferedFraction)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                // Active
                Box(
                    Modifier
                        .fillMaxWidth(fraction = progressFraction)
                        // TODO: verify dimension with UX
                        .height(3.165.dp)
                        .background(MaterialTheme.colorScheme.onPrimary)
                )
            }
        },
        thumb = {
            // Overall thumb needs to be at least 16dp or else will be misaligned.
            Box(
                Modifier.height(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary)
                )
            }
        },
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val offset = 15.dp.roundToPx()
                layout(placeable.width, placeable.height - offset) {
                    placeable.place(0, -offset)
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatDuration(progressMs),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.clearAndSetSemantics { },
        )
        Text(
            text = formatDuration(durationMs),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.clearAndSetSemantics { },
        )
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    return android.text.format.DateUtils.formatElapsedTime(totalSeconds)
}
