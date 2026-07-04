package com.armanmaurya.internetradio.ui.tv.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.armanmaurya.internetradio.player.PlaybackSource
import com.armanmaurya.internetradio.ui.mobile.screens.home.HomeViewModel
import com.armanmaurya.internetradio.ui.shared.viewmodels.AddedViewModel
import com.armanmaurya.internetradio.ui.shared.viewmodels.BrowseViewModel
import com.armanmaurya.internetradio.ui.shared.viewmodels.FavoritesViewModel
import com.armanmaurya.internetradio.ui.shared.viewmodels.PlayerViewModel
import com.armanmaurya.internetradio.ui.shared.viewmodels.RecentViewModel
import com.armanmaurya.internetradio.ui.tv.screens.added.AddedScreen
import com.armanmaurya.internetradio.ui.tv.screens.browse.BrowseScreen
import com.armanmaurya.internetradio.ui.tv.screens.countries.CountrySelectScreen
import com.armanmaurya.internetradio.ui.tv.screens.favorites.FavoritesScreen
import com.armanmaurya.internetradio.ui.tv.screens.languages.LanguageSelectScreen
import com.armanmaurya.internetradio.ui.tv.screens.player.PlayerScreen
import com.armanmaurya.internetradio.ui.tv.screens.recent.RecentScreen
import com.armanmaurya.internetradio.ui.tv.screens.settings.SettingsScreen
import com.armanmaurya.internetradio.ui.tv.screens.tags.TagSelectScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    browseViewModel: BrowseViewModel,
    recentViewModel: RecentViewModel,
    favoritesViewModel: FavoritesViewModel,
    addedViewModel: AddedViewModel,
    playerViewModel: PlayerViewModel,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by playerViewModel.playbackState.collectAsStateWithLifecycle()
    val playingStationUuid = playbackState.currentStation?.stationUuid
    val isPlaybackActive = playbackState.isPlaying

    NavHost(
        navController = navController,
        startDestination = AppDestination.Browse.route,
        modifier = modifier
    ) {
        composable(AppDestination.Browse.route) {
            BrowseScreen(
                viewModel = browseViewModel,
                playingStationUuid = playingStationUuid,
                isPlaybackActive = isPlaybackActive,
                onStationClick = { stations, index, _ -> playerViewModel.play(stations, index, PlaybackSource.None) }
            )
        }
        composable(AppDestination.Recent.route) {
            RecentScreen(
                viewModel = recentViewModel,
                playingStationUuid = playingStationUuid,
                isPlaybackActive = isPlaybackActive,
                onStationClick = { stations, index, _ -> playerViewModel.play(stations, index, PlaybackSource.Recent) }
            )
        }
        composable(AppDestination.Favorites.route) {
            FavoritesScreen(
                viewModel = favoritesViewModel,
                playingStationUuid = playingStationUuid,
                isPlaybackActive = isPlaybackActive,
                onStationClick = { stations, index, _ -> playerViewModel.play(stations, index, PlaybackSource.Favorites) }
            )
        }
        composable(AppDestination.Added.route) {
            AddedScreen(
                viewModel = addedViewModel,
                playingStationUuid = playingStationUuid,
                isPlaybackActive = isPlaybackActive,
                onStationClick = { stations, index, _ -> playerViewModel.play(stations, index, PlaybackSource.None) }
            )
        }
        composable(AppDestination.Settings.route) {
            SettingsScreen()
        }
        composable(AppDestination.Player.route) {
            PlayerScreen(
                playerViewModel = playerViewModel
            )
        }
        composable(AppDestination.Tags.route) {
            TagSelectScreen(
                initialTags = homeUiState.selectedTags,
                onTagsSelected = {
                    homeViewModel.updateTags(it)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(AppDestination.Language.route) {
            LanguageSelectScreen(
                selectedLanguage = homeUiState.selectedLanguage,
                onLanguageSelected = {
                    homeViewModel.updateLanguage(it.name)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(AppDestination.Country.route) {
            CountrySelectScreen(
                selectedCountryCode = homeUiState.selectedCountryCode,
                onCountrySelected = {
                    homeViewModel.updateCountry(it.isoCode)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
