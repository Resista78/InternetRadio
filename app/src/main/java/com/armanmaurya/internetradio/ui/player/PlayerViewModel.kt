package com.armanmaurya.internetradio.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armanmaurya.internetradio.data.model.RadioStation
import com.armanmaurya.internetradio.data.repository.FavoriteRepository
import com.armanmaurya.internetradio.data.repository.RecentRepository
import com.armanmaurya.internetradio.data.repository.StationRepository
import com.armanmaurya.internetradio.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val stationRepository: StationRepository
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
                        play(freshStation)
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

    fun play(station: RadioStation) {
        playerController.play(station)
        viewModelScope.launch {
            recentRepository.addRecentStation(station)
            stationRepository.registerClick(station.stationUuid)
        }
    }

    fun togglePlayPause() {
        playerController.togglePlayPause()
    }

    fun stop() {
        playerController.stop()
    }
}
