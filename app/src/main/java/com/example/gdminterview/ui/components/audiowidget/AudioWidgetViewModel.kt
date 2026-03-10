package com.example.gdminterview.ui.components.audiowidget

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gdminterview.data.SongRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AudioWidgetUiState(
    val songName: String,
    val artistName: String,
    val albumName: String,
    val albumArtKey: String?,
    val durationMs: Long,
    val progressMs: Long,
    val bufferedMs: Long,
    val isPlaying: Boolean,
    val hasNext: Boolean,
    val isFavorited: Boolean,
    val repeatAll: Boolean,
) {
    val progressFraction: Float get() = if (durationMs > 0) progressMs.toFloat() / durationMs else 0f
    val bufferedFraction: Float get() = if (durationMs > 0) bufferedMs.toFloat() / durationMs else 0f
}

private data class PlayerState(
    val songIndex: Int = 0,
    val progressMs: Long = 132_000L,
    val bufferedMs: Long = 146_580L,
    val isPlaying: Boolean = false,
    val favoritedIds: Set<String> = emptySet(),
    val repeatAll: Boolean = false,
)

class AudioWidgetViewModel : ViewModel() {
    private val songs = SongRepository.songs

    private val _state = MutableStateFlow(PlayerState())

    private var playbackJob: Job? = null
    private var bufferJob: Job? = null

    val uiState: StateFlow<AudioWidgetUiState> = _state.map { s ->
        val song = songs[s.songIndex]
        AudioWidgetUiState(
            songName = song.name,
            artistName = song.artist,
            albumName = song.albumName,
            albumArtKey = song.albumArtKey,
            durationMs = song.durationMs,
            progressMs = s.progressMs,
            bufferedMs = s.bufferedMs,
            isPlaying = s.isPlaying,
            hasNext = s.repeatAll || s.songIndex < songs.size - 1,
            isFavorited = song.id in s.favoritedIds,
            repeatAll = s.repeatAll,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value.let { s ->
            val song = songs[s.songIndex]
            AudioWidgetUiState(
                songName = song.name,
                artistName = song.artist,
                albumName = song.albumName,
                albumArtKey = song.albumArtKey,
                durationMs = song.durationMs,
                progressMs = s.progressMs,
                bufferedMs = s.bufferedMs,
                isPlaying = s.isPlaying,
                hasNext = s.repeatAll || s.songIndex < songs.size - 1,
                isFavorited = song.id in s.favoritedIds,
                repeatAll = s.repeatAll,
            )
        }
    )

    fun onProgressChange(valueMs: Float) {
        val s = _state.value
        val durationMs = songs[s.songIndex].durationMs
        val coerced = valueMs.toLong().coerceIn(0L, durationMs)
        _state.update {
            it.copy(
                progressMs = coerced,
                bufferedMs = if (coerced > it.bufferedMs) coerced else it.bufferedMs,
            )
        }
        if (coerced > s.bufferedMs) {
            bufferJob?.cancel()
            bufferJob = null
        }
        maybeStartBuffering()
    }

    fun togglePlayPause() {
        if (_state.value.isPlaying) pause() else play()
    }

    private fun play() {
        _state.update { it.copy(isPlaying = true) }
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            var lastTick = SystemClock.elapsedRealtime()
            while (_state.value.isPlaying) {
                delay(100)
                val now = SystemClock.elapsedRealtime()
                val elapsed = now - lastTick
                lastTick = now
                val s = _state.value
                val durationMs = songs[s.songIndex].durationMs
                _state.update {
                    it.copy(progressMs = (it.progressMs + elapsed).coerceAtMost(durationMs))
                }
                maybeStartBuffering()
                if (_state.value.progressMs >= durationMs) {
                    if (_state.value.songIndex < songs.size - 1) {
                        skipNext()
                        lastTick = SystemClock.elapsedRealtime()
                    } else if (_state.value.repeatAll) {
                        loadSong(0)
                        maybeStartBuffering()
                        lastTick = SystemClock.elapsedRealtime()
                    } else {
                        pause()
                    }
                }
            }
        }
    }

    private fun pause() {
        _state.update { it.copy(isPlaying = false) }
        playbackJob?.cancel()
        playbackJob = null
    }

    private fun maybeStartBuffering() {
        if (bufferJob != null) return
        val s = _state.value
        val durationMs = songs[s.songIndex].durationMs
        if (s.bufferedMs >= durationMs) return
        if (s.bufferedMs - s.progressMs > BUFFER_THRESHOLD_MS) return
        bufferJob = viewModelScope.launch {
            try {
                val target = (_state.value.bufferedMs + BUFFER_CHUNK_MS).coerceAtMost(durationMs)
                while (_state.value.bufferedMs < target) {
                    delay(100)
                    _state.update {
                        it.copy(bufferedMs = (it.bufferedMs + BUFFER_RATE_PER_TICK_MS).coerceAtMost(target))
                    }
                }
            } finally {
                bufferJob = null
            }
        }
    }

    private fun loadSong(index: Int) {
        bufferJob?.cancel()
        bufferJob = null
        _state.update { it.copy(songIndex = index, progressMs = 0L, bufferedMs = 0L) }
    }

    fun toggleRepeat() {
        _state.update { it.copy(repeatAll = !it.repeatAll) }
    }

    fun toggleFavorite() {
        val id = songs[_state.value.songIndex].id
        _state.update {
            it.copy(
                favoritedIds = if (id in it.favoritedIds) it.favoritedIds - id
                else it.favoritedIds + id
            )
        }
    }

    fun skipPrevious() {
        val s = _state.value
        if (s.songIndex == 0 || s.progressMs > 3_000L) {
            _state.update { it.copy(progressMs = 0L) }
        } else {
            loadSong(s.songIndex - 1)
        }
        maybeStartBuffering()
    }

    fun skipNext() {
        val s = _state.value
        if (s.songIndex < songs.size - 1) {
            loadSong(s.songIndex + 1)
        } else if (s.repeatAll) {
            loadSong(0)
        } else {
            return
        }
        maybeStartBuffering()
    }

    companion object {
        private const val BUFFER_THRESHOLD_MS = 10_000L
        private const val BUFFER_CHUNK_MS = 30_000L
        private const val BUFFER_RATE_PER_TICK_MS = 3_000L

    }
}
