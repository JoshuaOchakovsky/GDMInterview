package com.example.gdminterview.ui.components.audiowidget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.gdminterview.R
import com.example.gdminterview.ui.components.MaterialSymbol
import com.example.gdminterview.ui.components.MaterialSymbolIcon

private val COMPACT_BREAKPOINT = 400.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioWidget(viewModel: AudioWidgetViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()
    BoxWithConstraints(modifier.widthIn(max = 480.dp)) {
        val compact = maxWidth < COMPACT_BREAKPOINT
        val contentPadding = if (compact) 20.dp else 32.dp
        val albumArtSize = if (compact) 64.dp else 88.dp
        val playButtonSize = if (compact) 56.dp else 72.dp
        val playIconSize = if (compact) 36.dp else 48.dp

        Box(
            Modifier
                .clip(MaterialTheme.shapes.large)
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(contentPadding)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AlbumArt(
                        albumArtKey = state.albumArtKey,
                        albumName = state.albumName,
                        songName = state.songName,
                        size = albumArtSize,
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            state.songName,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            state.artistName,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            // TODO: confirm right padding, current value is a guess based on text
                            //   overflow behavior in Figma.
                            modifier = Modifier.padding(end = 40.dp)
                        )
                    }
                }
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Spacer(Modifier.height(13.dp))
                    AudioSlider(
                        progressMs = state.progressMs,
                        durationMs = state.durationMs,
                        bufferedFraction = state.bufferedFraction,
                        progressFraction = state.progressFraction,
                        onProgressChange = viewModel::onProgressChange,
                    )
                    Spacer(Modifier.height(12.dp))
                    PlaybackControls(
                        state = state,
                        viewModel = viewModel,
                        compact = compact,
                        playButtonSize = playButtonSize,
                        playIconSize = playIconSize,
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumArt(
    albumArtKey: String?,
    albumName: String,
    songName: String,
    size: Dp,
) {
    val albumArtModifier = Modifier
        .size(size)
        .clip(MaterialTheme.shapes.extraSmall)
    val albumArtResId = albumArtKey?.let { albumArtFor(it) }
    if (albumArtResId != null) {
        Image(
            painter = painterResource(albumArtResId),
            contentDescription = "Album art for $songName",
            modifier = albumArtModifier,
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = albumArtModifier
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = albumName.take(1).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaybackControls(
    state: AudioWidgetUiState,
    viewModel: AudioWidgetViewModel,
    compact: Boolean,
    playButtonSize: Dp,
    playIconSize: Dp,
) {
    // UX spec: 24dp between 36dp IconButtons and 72dp FilledIconButton, but IconButtons are
    // actually min. 48dp for a11y, so gaps are reduced by 6dp per oversized side:
    // IconButton <-> IconButton = 24 - (6 * 2) = 12dp
    // IconButton <-> FilledIconButton = 24 - 6 = 18dp
    // In compact mode, use SpaceEvenly to fit within narrower widths.
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (compact) Arrangement.SpaceEvenly else Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconButtonRipple = RippleConfiguration(
            color = MaterialTheme.colorScheme.onPrimary
        )
        CompositionLocalProvider(LocalRippleConfiguration provides iconButtonRipple) {
            IconButton(onClick = viewModel::toggleRepeat) {
                MaterialSymbolIcon(
                    MaterialSymbol.Repeat,
                    contentDescription = if (state.repeatAll) "Repeat all on" else "Repeat off",
                    size = 24.dp,
                    tint = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = if (state.repeatAll) 1f else 0.5f
                    )
                )
            }
            if (!compact) Spacer(Modifier.width(12.dp))
            IconButton(onClick = viewModel::skipPrevious) {
                MaterialSymbolIcon(
                    MaterialSymbol.SkipPrevious,
                    contentDescription = "Previous",
                    size = 36.dp,
                    fill = true,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        if (!compact) Spacer(Modifier.width(18.dp))
        FilledIconButton(
            onClick = viewModel::togglePlayPause,
            modifier = Modifier.requiredSize(playButtonSize)
        ) {
            MaterialSymbolIcon(
                if (state.isPlaying) MaterialSymbol.Pause
                else MaterialSymbol.PlayArrow,
                contentDescription = if (state.isPlaying) "Pause" else "Play",
                size = playIconSize,
                fill = true,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        if (!compact) Spacer(Modifier.width(18.dp))
        CompositionLocalProvider(LocalRippleConfiguration provides iconButtonRipple) {
            IconButton(onClick = viewModel::skipNext, enabled = state.hasNext) {
                MaterialSymbolIcon(
                    MaterialSymbol.SkipNext,
                    contentDescription = "Skip",
                    size = 36.dp,
                    fill = true,
                    tint = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = if (state.hasNext) 1f else 0.38f
                    )
                )
            }
            if (!compact) Spacer(Modifier.width(12.dp))
            IconButton(onClick = viewModel::toggleFavorite) {
                MaterialSymbolIcon(
                    MaterialSymbol.Favorite,
                    contentDescription = if (state.isFavorited) "Unfavorite" else "Favorite",
                    size = 24.dp,
                    fill = state.isFavorited,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

private fun albumArtFor(key: String): Int? = when (key) {
    "purple_noon" -> R.drawable.album_purple_noon
    "little_night_music" -> R.drawable.album_little_night_music
    "company" -> R.drawable.album_company
    "follies" -> R.drawable.album_follies
    else -> null
}