package com.armanmaurya.internetradio.ui.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import coil3.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.basicMarquee
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.painterResource
import com.armanmaurya.internetradio.R
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.tv.material3.DrawerValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerItemDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.armanmaurya.internetradio.player.PlaybackSource
import com.armanmaurya.internetradio.ui.player.PlayerViewModel
import com.armanmaurya.internetradio.ui.screens.home.tabs.added.AddedViewModel
import com.armanmaurya.internetradio.ui.screens.home.tabs.browse.BrowseViewModel
import com.armanmaurya.internetradio.ui.screens.home.tabs.favorites.FavoritesViewModel
import com.armanmaurya.internetradio.ui.screens.home.tabs.recent.RecentViewModel
import com.armanmaurya.internetradio.ui.theme.TvInternetRadioTheme
import com.armanmaurya.internetradio.ui.tv.screens.TvAddedScreen
import com.armanmaurya.internetradio.ui.tv.screens.TvBrowseScreen
import com.armanmaurya.internetradio.ui.tv.screens.TvFavoritesScreen
import com.armanmaurya.internetradio.ui.tv.screens.TvRecentScreen
import com.armanmaurya.internetradio.ui.tv.screens.TvPlayerScreen
import com.armanmaurya.internetradio.ui.tv.screens.TvSettingsScreen

enum class TvScreen {
    BROWSE, RECENT, FAVORITES, ADDED, SETTINGS, PLAYER
}

@Composable
fun TvApp(
    browseViewModel: BrowseViewModel = hiltViewModel(),
    recentViewModel: RecentViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    addedViewModel: AddedViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    var currentScreen by remember { mutableStateOf(TvScreen.BROWSE) }
    
    val playbackState by playerViewModel.playbackState.collectAsStateWithLifecycle()
    val playingStationUuid = playbackState.currentStation?.stationUuid
    val isPlaybackActive = playbackState.isPlaying

    if (currentScreen != TvScreen.BROWSE) {
        BackHandler {
            currentScreen = TvScreen.BROWSE
        }
    }

    TvInternetRadioTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModalNavigationDrawer(
                drawerContent = { drawerValue ->
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = 280.dp)
                            .background(
                                if (currentScreen == TvScreen.PLAYER) {
                                    if (drawerValue == DrawerValue.Closed) Color.Transparent else Color.Black.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .padding(12.dp)
                    ) {
                        if (playbackState.currentStation != null) {
                            Surface(
                                onClick = { currentScreen = TvScreen.PLAYER },
                                scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
                                colors = ClickableSurfaceDefaults.colors(
                                    containerColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                                    pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
                                ),
                                shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(8.dp)),
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val fallbackPainter = painterResource(id = R.drawable.ic_launcher_foreground)
                                    AsyncImage(
                                        model = playbackState.currentStation!!.favicon.ifEmpty { null },
                                        contentDescription = "Station Thumbnail",
                                        placeholder = fallbackPainter,
                                        error = fallbackPainter,
                                        fallback = fallbackPainter,
                                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentScale = ContentScale.Crop
                                    )
                                    
                                    if (drawerValue == DrawerValue.Open) {
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = playbackState.currentStation!!.name,
                                                maxLines = 1,
                                                modifier = Modifier.basicMarquee(),
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            if (!playbackState.currentTrack.isNullOrEmpty()) {
                                                Text(
                                                    text = playbackState.currentTrack!!,
                                                    maxLines = 1,
                                                    modifier = Modifier.basicMarquee(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        NavigationDrawerItem(
                            selected = currentScreen == TvScreen.BROWSE,
                            onClick = { currentScreen = TvScreen.BROWSE },
                            shape = NavigationDrawerItemDefaults.shape(shape = RoundedCornerShape(8.dp)),
                            leadingContent = {
                                Icon(imageVector = Icons.Default.Explore, contentDescription = "Browse")
                            }
                        ) {
                            Text("Browse")
                        }
                        
                        NavigationDrawerItem(
                            selected = currentScreen == TvScreen.RECENT,
                            onClick = { currentScreen = TvScreen.RECENT },
                            shape = NavigationDrawerItemDefaults.shape(shape = RoundedCornerShape(8.dp)),
                            leadingContent = {
                                Icon(imageVector = Icons.Default.History, contentDescription = "Recent")
                            }
                        ) {
                            Text("Recent")
                        }

                        NavigationDrawerItem(
                            selected = currentScreen == TvScreen.FAVORITES,
                            onClick = { currentScreen = TvScreen.FAVORITES },
                            shape = NavigationDrawerItemDefaults.shape(shape = RoundedCornerShape(8.dp)),
                            leadingContent = {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorites")
                            }
                        ) {
                            Text("Favorites")
                        }

                        NavigationDrawerItem(
                            selected = currentScreen == TvScreen.ADDED,
                            onClick = { currentScreen = TvScreen.ADDED },
                            shape = NavigationDrawerItemDefaults.shape(shape = RoundedCornerShape(8.dp)),
                            leadingContent = {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Added")
                            }
                        ) {
                            Text("Added")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        NavigationDrawerItem(
                            selected = currentScreen == TvScreen.SETTINGS,
                            onClick = { currentScreen = TvScreen.SETTINGS },
                            shape = NavigationDrawerItemDefaults.shape(shape = RoundedCornerShape(8.dp)),
                            leadingContent = {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                            }
                        ) {
                            Text("Settings")
                        }
                    }
                }
            ) {
                // Content area
                val contentPadding = if (currentScreen == TvScreen.PLAYER) 0.dp else 96.dp
                Box(modifier = Modifier.fillMaxSize().padding(start = contentPadding)) {
                    when (currentScreen) {
                        TvScreen.BROWSE -> TvBrowseScreen(
                            viewModel = browseViewModel,
                            playingStationUuid = playingStationUuid,
                            isPlaybackActive = isPlaybackActive,
                            onStationClick = { stations, index, _ -> playerViewModel.play(stations, index, PlaybackSource.None) }
                        )
                        TvScreen.RECENT -> TvRecentScreen(
                            viewModel = recentViewModel,
                            playingStationUuid = playingStationUuid,
                            isPlaybackActive = isPlaybackActive,
                            onStationClick = { stations, index, _ -> playerViewModel.play(stations, index, PlaybackSource.Recent) }
                        )
                        TvScreen.FAVORITES -> TvFavoritesScreen(
                            viewModel = favoritesViewModel,
                            playingStationUuid = playingStationUuid,
                            isPlaybackActive = isPlaybackActive,
                            onStationClick = { stations, index, _ -> playerViewModel.play(stations, index, PlaybackSource.Favorites) }
                        )
                        TvScreen.ADDED -> TvAddedScreen(
                            viewModel = addedViewModel,
                            playingStationUuid = playingStationUuid,
                            isPlaybackActive = isPlaybackActive,
                            onStationClick = { stations, index, _ -> playerViewModel.play(stations, index, PlaybackSource.None) }
                        )
                        TvScreen.SETTINGS -> TvSettingsScreen()
                        TvScreen.PLAYER -> TvPlayerScreen(
                            playerViewModel = playerViewModel
                        )
                    }
                }
            }
        }
    }
}
