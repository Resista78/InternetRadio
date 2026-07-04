package com.armanmaurya.internetradio.ui.tv.screens.home

import android.view.KeyEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import coil3.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.input.ImeAction
import androidx.tv.material3.Border
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerItemDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import com.armanmaurya.internetradio.ui.shared.viewmodels.PlayerViewModel
import com.armanmaurya.internetradio.ui.mobile.screens.home.HomeViewModel
import com.armanmaurya.internetradio.ui.shared.viewmodels.AddedViewModel
import com.armanmaurya.internetradio.ui.shared.viewmodels.BrowseViewModel
import com.armanmaurya.internetradio.ui.shared.viewmodels.FavoritesViewModel
import com.armanmaurya.internetradio.ui.shared.viewmodels.RecentViewModel
import com.armanmaurya.internetradio.ui.shared.theme.TvInternetRadioTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.armanmaurya.internetradio.ui.tv.navigation.AppDestination
import com.armanmaurya.internetradio.ui.tv.navigation.AppNavHost

@Composable
fun HomeScreen(
    browseViewModel: BrowseViewModel = hiltViewModel(),
    recentViewModel: RecentViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    addedViewModel: AddedViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route ?: AppDestination.Browse.route

    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val contentFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            contentFocusRequester.requestFocus()
        } catch (e: Exception) {
            // Ignore focus error on init
        }
    }
    
    val playbackState by playerViewModel.playbackState.collectAsStateWithLifecycle()
    
    // Helper for drawer navigation
    val navigateToDrawerItem: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        coroutineScope.launch { drawerState.setValue(DrawerValue.Closed) }
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
                                if (currentDestination == AppDestination.Player.route) {
                                    if (drawerValue == DrawerValue.Closed) Color.Transparent else Color.Black.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .padding(12.dp)
                    ) {
                        if (playbackState.currentStation != null) {
                            Surface(
                                onClick = { navigateToDrawerItem(AppDestination.Player.route) },
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
                        val searchBarWidth by androidx.compose.animation.core.animateDpAsState(
                            targetValue = if (drawerValue == DrawerValue.Open) 256.dp else 56.dp,
                            label = "searchBarWidth"
                        )

                        if (isSearchEditing && drawerValue == DrawerValue.Open) {
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
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = { isSearchEditing = false }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.border,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedPlaceholderColor = Color.Gray,
                                    unfocusedPlaceholderColor = Color.Gray
                                )
                            )
                            LaunchedEffect(Unit) {
                                searchFocusRequester.requestFocus()
                            }
                            androidx.activity.compose.BackHandler {
                                isSearchEditing = false
                            }
                        } else {
                            Surface(
                                onClick = { 
                                    isSearchEditing = true
                                    coroutineScope.launch { drawerState.setValue(DrawerValue.Open) }
                                },
                                modifier = Modifier
                                    .padding(bottom = 12.dp)
                                    .width(searchBarWidth),
                                shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(8.dp)),
                                colors = ClickableSurfaceDefaults.colors(
                                    containerColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                                ),
                                border = ClickableSurfaceDefaults.border(
                                    border = Border(
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.border),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                    focusedBorder = Border(
                                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                ),
                                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = if (drawerValue == DrawerValue.Open) Arrangement.Start else Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (drawerValue == DrawerValue.Open) {
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Search",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }

                        if (drawerValue == DrawerValue.Open) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                IconButton(
                                    onClick = { navigateToDrawerItem(AppDestination.Tags.route) },
                                    modifier = Modifier.weight(1f).height(56.dp).background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)),
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
                                    onClick = { navigateToDrawerItem(AppDestination.Language.route) },
                                    modifier = Modifier.weight(1f).height(56.dp).background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)),
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
                                    onClick = { navigateToDrawerItem(AppDestination.Country.route) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                                        .onKeyEvent { event ->
                                            if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
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
                        } else {
                            NavigationDrawerItem(
                                selected = false,
                                onClick = { coroutineScope.launch { drawerState.setValue(DrawerValue.Open) } },
                                shape = NavigationDrawerItemDefaults.shape(shape = RoundedCornerShape(8.dp)),
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = "Filters",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {}
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        NavigationDrawerItem(
                            selected = currentDestination == AppDestination.Browse.route,
                            onClick = { navigateToDrawerItem(AppDestination.Browse.route) },
                            shape = NavigationDrawerItemDefaults.shape(shape = RoundedCornerShape(8.dp)),
                            leadingContent = {
                                Icon(imageVector = Icons.Default.Explore, contentDescription = "Browse")
                            }
                        ) {
                            Text("Browse")
                        }
                        
                        NavigationDrawerItem(
                            selected = currentDestination == AppDestination.Recent.route,
                            onClick = { navigateToDrawerItem(AppDestination.Recent.route) },
                            shape = NavigationDrawerItemDefaults.shape(shape = RoundedCornerShape(8.dp)),
                            leadingContent = {
                                Icon(imageVector = Icons.Default.History, contentDescription = "Recent")
                            }
                        ) {
                            Text("Recent")
                        }

                        NavigationDrawerItem(
                            selected = currentDestination == AppDestination.Favorites.route,
                            onClick = { navigateToDrawerItem(AppDestination.Favorites.route) },
                            shape = NavigationDrawerItemDefaults.shape(shape = RoundedCornerShape(8.dp)),
                            leadingContent = {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorites")
                            }
                        ) {
                            Text("Favorites")
                        }

                        NavigationDrawerItem(
                            selected = currentDestination == AppDestination.Added.route,
                            onClick = { navigateToDrawerItem(AppDestination.Added.route) },
                            shape = NavigationDrawerItemDefaults.shape(shape = RoundedCornerShape(8.dp)),
                            leadingContent = {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Added")
                            }
                        ) {
                            Text("Added")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        NavigationDrawerItem(
                            selected = currentDestination == AppDestination.Settings.route,
                            onClick = { navigateToDrawerItem(AppDestination.Settings.route) },
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
                val contentPadding = if (currentDestination == AppDestination.Player.route) 0.dp else 96.dp
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
                    AppNavHost(
                        navController = navController,
                        browseViewModel = browseViewModel,
                        recentViewModel = recentViewModel,
                        favoritesViewModel = favoritesViewModel,
                        addedViewModel = addedViewModel,
                        playerViewModel = playerViewModel,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }
}
