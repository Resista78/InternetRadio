package com.armanmaurya.internetradio.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.armanmaurya.internetradio.data.model.RadioStation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerController @Inject constructor(
    private val player: Player
) {
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState = _playbackState.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlaybackStateChanged(state: Int) {
                _playbackState.update {
                    it.copy(
                        isLoading = state == Player.STATE_BUFFERING,
                        isError = state == Player.STATE_IDLE && player.playerError != null
                    )
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                _playbackState.update {
                    it.copy(
                        currentStation = mediaItem?.toRadioStation()
                    )
                }
            }
        })
    }

    fun play(station: RadioStation) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(station.stationUuid)
            .setUri(station.urlResolved)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(station.name)
                    .setArtworkUri(android.net.Uri.parse(station.favicon))
                    .build()
            )
            .setTag(station)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun stop() {
        player.stop()
        player.clearMediaItems()
    }

    private fun MediaItem.toRadioStation(): RadioStation? {
        return localConfiguration?.tag as? RadioStation
    }
}

data class PlaybackState(
    val currentStation: RadioStation? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)
