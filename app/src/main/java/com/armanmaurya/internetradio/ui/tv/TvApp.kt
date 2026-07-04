package com.armanmaurya.internetradio.ui.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
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
import androidx.tv.material3.rememberDrawerState
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
import com.armanmaurya.internetradio.ui.screens.home.HomeViewModel
import com.armanmaurya.internetradio.ui.tv.screens.TvLanguageSelectScreen
import com.armanmaurya.internetradio.ui.tv.screens.TvTagSelectScreen
import com.armanmaurya.internetradio.ui.tv.screens.TvCountrySelectScreen

enum class TvScreen {
    BROWSE, RECENT, FAVORITES, ADDED, SETTINGS, PLAYER, TAGS, LANGUAGE, COUNTRY
}

@Composable
fun TvApp(
    browseViewModel: BrowseViewModel = hiltViewModel(),
    recentViewModel: RecentViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    addedViewModel: AddedViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    var currentScreen by remember { mutableStateOf(TvScreen.BROWSE) }
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val contentFocusRequester = remember { FocusRequester() }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        try {
            contentFocusRequester.requestFocus()
        } catch (e: Exception) {
            // Ignore focus error on init
        }
    }
    
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
                drawerState = drawerState,
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
                                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
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

                        var isSearchEditing by remember { mutableStateOf(false) }
                        val searchFocusRequester = remember { FocusRequester() }

                        if (drawerValue == DrawerValue.Open) {
                            if (isSearchEditing) {
                                var hasGainedFocus by remember { mutableStateOf(false) }
                                OutlinedTextField(
                                    value = homeUiState.searchQuery,
                                    onValueChange = { homeViewModel.onSearchQueryChange(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                        .focusRequester(searchFocusRequester)
                                        .onFocusChanged { state ->
                                            if (state.isFocused) {
                                                hasGainedFocus = true
                                            } else if (hasGainedFocus) {
                                                isSearchEditing = false
                                            }
                                        },
                                    placeholder = { Text("Search...") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search"
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        imeAction = androidx.compose.ui.text.input.ImeAction.Search
                                    ),
                                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                        onSearch = { isSearchEditing = false }
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.border,
                                        focusedTextColor = androidx.compose.ui.graphics.Color.White,
                                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                                        focusedPlaceholderColor = androidx.compose.ui.graphics.Color.Gray,
                                        unfocusedPlaceholderColor = androidx.compose.ui.graphics.Color.Gray
                                    )
                                )
                                androidx.compose.runtime.LaunchedEffect(Unit) {
                                    searchFocusRequester.requestFocus()
                                }
                                BackHandler {
                                    isSearchEditing = false
                                }
                            } else {
                                Surface(
                                    onClick = { isSearchEditing = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(8.dp)),
                                    colors = ClickableSurfaceDefaults.colors(
                                        containerColor = Color.Transparent,
                                        focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                                    ),
                                    border = ClickableSurfaceDefaults.border(
                                        border = androidx.tv.material3.Border(
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.border),
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                        focusedBorder = androidx.tv.material3.Border(
                                            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    ),
                                    scale = ClickableSurfaceDefaults.scale(focusedScale = 1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = if (homeUiState.searchQuery.isNotEmpty()) homeUiState.searchQuery else "Search...",
                                            color = if (homeUiState.searchQuery.isNotEmpty()) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Gray,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                IconButton(
                                    onClick = { 
                                        currentScreen = TvScreen.TAGS 
                                        coroutineScope.launch { drawerState.setValue(DrawerValue.Closed) }
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    scale = IconButtonDefaults.scale(focusedScale = 1f),
                                    shape = IconButtonDefaults.shape(shape = RoundedCornerShape(8.dp))
                                ) {
                                    Box {
                                        Icon(
                                            Icons.Default.Label, 
                                            contentDescription = "Tags",
                                            tint = if (homeUiState.selectedTags.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (homeUiState.selectedTags.isNotEmpty()) {
                                            Text(
                                                text = homeUiState.selectedTags.size.toString(),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 6.dp, y = (-4).dp)
                                            )
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { 
                                        currentScreen = TvScreen.LANGUAGE 
                                        coroutineScope.launch { drawerState.setValue(DrawerValue.Closed) }
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    scale = IconButtonDefaults.scale(focusedScale = 1f),
                                    shape = IconButtonDefaults.shape(shape = RoundedCornerShape(8.dp))
                                ) {
                                    Box {
                                        Icon(
                                            Icons.Default.Language, 
                                            contentDescription = "Language",
                                            tint = if (!homeUiState.selectedLanguage.isNullOrBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (!homeUiState.selectedLanguage.isNullOrBlank()) {
                                            Text(
                                                text = homeUiState.selectedLanguage!!.take(2).uppercase(),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 6.dp, y = (-4).dp)
                                            )
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { 
                                        currentScreen = TvScreen.COUNTRY 
                                        coroutineScope.launch { drawerState.setValue(DrawerValue.Closed) }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .onKeyEvent { event ->
                                            if (event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DPAD_RIGHT && event.nativeKeyEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                                                try { contentFocusRequester.requestFocus() } catch(e: Exception) {}
                                                coroutineScope.launch { drawerState.setValue(DrawerValue.Closed) }
                                                true
                                            } else {
                                                false
                                            }
                                        },
                                    scale = IconButtonDefaults.scale(focusedScale = 1f),
                                    shape = IconButtonDefaults.shape(shape = RoundedCornerShape(8.dp))
                                ) {
                                    Box {
                                        Icon(
                                            Icons.Default.Public, 
                                            contentDescription = "Country",
                                            tint = if (!homeUiState.selectedCountryCode.isNullOrBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (!homeUiState.selectedCountryCode.isNullOrBlank()) {
                                            Text(
                                                text = homeUiState.selectedCountryCode!!,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 6.dp, y = (-4).dp)
                                            )
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = contentPadding)
                        .focusRequester(contentFocusRequester)
                        .focusProperties {
                            // If focus enters the Box, let the children handle it
                            canFocus = false
                        }
                ) {
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
                        TvScreen.TAGS -> TvTagSelectScreen(
                            initialTags = homeUiState.selectedTags,
                            onTagsSelected = {
                                homeViewModel.updateTags(it)
                                currentScreen = TvScreen.BROWSE
                            },
                            onBackClick = { currentScreen = TvScreen.BROWSE }
                        )
                        TvScreen.LANGUAGE -> TvLanguageSelectScreen(
                            selectedLanguage = homeUiState.selectedLanguage,
                            onLanguageSelected = {
                                homeViewModel.updateLanguage(it.name)
                                currentScreen = TvScreen.BROWSE
                            },
                            onBackClick = { currentScreen = TvScreen.BROWSE }
                        )
                        TvScreen.COUNTRY -> TvCountrySelectScreen(
                            selectedCountryCode = homeUiState.selectedCountryCode,
                            onCountrySelected = {
                                homeViewModel.updateCountry(it.isoCode)
                                currentScreen = TvScreen.BROWSE
                            },
                            onBackClick = { currentScreen = TvScreen.BROWSE }
                        )
                    }
                }
            }
        }
    }
}
