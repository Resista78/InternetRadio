package com.armanmaurya.internetradio.ui.shared.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armanmaurya.internetradio.data.model.RadioStation
import com.armanmaurya.internetradio.data.repository.FavoriteRepository
import com.armanmaurya.internetradio.data.repository.RecentRepository
import com.armanmaurya.internetradio.data.repository.StationRepository
import com.armanmaurya.internetradio.data.repository.TrackHistoryRepository
import com.armanmaurya.internetradio.player.PlaybackSource
import com.armanmaurya.internetradio.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val stationRepository: StationRepository,
    private val trackHistoryRepository: TrackHistoryRepository
) : ViewModel() {

    val playbackState = playerController.playbackState

    init {
        playbackState
            .map { it.isError }
            .distinctUntilChanged()
            .onEach { isError ->
                if (isError) {
                    handlePlaybackFailure()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun handlePlaybackFailure() {
        val currentStation = playbackState.value.currentStation ?: return
        viewModelScope.launch {
            stationRepository.getStationsByUuid(listOf(currentStation.stationUuid))
                .onSuccess { freshStations ->
                    val freshStation = freshStations.firstOrNull() ?: return@onSuccess

                    val hasChanged = freshStation.name != currentStation.name ||
                            freshStation.url != currentStation.url ||
                            freshStation.urlResolved != currentStation.urlResolved ||
                            freshStation.favicon != currentStation.favicon ||
                            freshStation.tags != currentStation.tags ||
                            freshStation.country != currentStation.country ||
                            freshStation.language != currentStation.language ||
                            freshStation.codec != currentStation.codec ||
                            freshStation.bitrate != currentStation.bitrate

                    if (hasChanged) {
                        // Update Favorite if it exists
                        if (favoriteRepository.isFavoriteDirect(currentStation.stationUuid)) {
                            favoriteRepository.addFavorite(freshStation)
                        }

                        // Update Recent
                        recentRepository.addRecentStation(freshStation)

                        // Re-trigger playback with fresh station
                        play(listOf(freshStation), 0)
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isFavorite = playbackState
        .map { it.currentStation?.stationUuid }
        .distinctUntilChanged()
        .flatMapLatest { uuid ->
            if (uuid == null) flowOf(false)
            else favoriteRepository.isFavorite(uuid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val trackHistory = playbackState
        .map { it.currentStation?.stationUuid }
        .distinctUntilChanged()
        .flatMapLatest { uuid ->
            if (uuid == null) flowOf(emptyList())
            else trackHistoryRepository.getTrackHistory(uuid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite() {
        val station = playbackState.value.currentStation ?: return
        viewModelScope.launch {
            if (isFavorite.value) {
                favoriteRepository.removeFavorite(station.stationUuid)
            } else {
                favoriteRepository.addFavorite(station)
            }
        }
    }

    fun play(stations: List<RadioStation>, startIndex: Int, source: PlaybackSource = PlaybackSource.None) {
        val station = stations[startIndex]
        playerController.play(stations, startIndex, source)
        viewModelScope.launch {
            recentRepository.addRecentStation(station)
            stationRepository.registerClick(station.stationUuid)
        }
    }

    fun next() {
        playerController.next()
    }

    fun previous() {
        playerController.previous()
    }

    fun togglePlayPause() {
        playerController.togglePlayPause()
    }

    fun stop() {
        playerController.stop()
    }

    fun setSleepTimer(durationMillis: Long) {
        playerController.setSleepTimer(durationMillis)
    }

    fun cancelSleepTimer() {
        playerController.cancelSleepTimer()
    }
}