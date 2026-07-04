package com.armanmaurya.internetradio.ui.tv.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.Text
import com.armanmaurya.internetradio.data.model.RadioStation
import com.armanmaurya.internetradio.ui.screens.home.tabs.recent.RecentViewModel
import com.armanmaurya.internetradio.ui.tv.components.TvStationCard

@Composable
fun TvRecentScreen(
    viewModel: RecentViewModel,
    playingStationUuid: String?,
    isPlaybackActive: Boolean,
    onStationClick: (List<RadioStation>, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stations by viewModel.recentStations.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        if (stations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No recent stations")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(
                    items = stations,
                    key = { _, station -> station.stationUuid }
                ) { index, station ->
                    TvStationCard(
                        station = station,
                        onClick = { onStationClick(stations, index, "tv_recent") },
                        isCurrentlyPlaying = station.stationUuid == playingStationUuid,
                        isPlaybackActive = isPlaybackActive && station.stationUuid == playingStationUuid
                    )
                }
            }
        }
    }
}
