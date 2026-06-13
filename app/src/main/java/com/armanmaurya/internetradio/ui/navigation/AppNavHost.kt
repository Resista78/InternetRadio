package com.armanmaurya.internetradio.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.armanmaurya.internetradio.ui.screens.discover.DiscoverScreen
import com.armanmaurya.internetradio.ui.screens.country.CountrySelectScreen
import com.armanmaurya.internetradio.ui.screens.country.LanguageSelectScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.armanmaurya.internetradio.ui.screens.discover.DiscoverViewModel
import com.armanmaurya.internetradio.ui.screens.settings.SettingsScreen
import com.armanmaurya.internetradio.ui.screens.settings.AboutScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val discoverViewModel: DiscoverViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = AppDestination.Discover.route,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        }
    ) {
        composable(AppDestination.Discover.route) {
            DiscoverScreen(
                viewModel = discoverViewModel,
                onSettingsClick = { navController.navigate(AppDestination.Settings.route) },
                onCountryClick = { 
                    val currentCode = discoverViewModel.uiState.value.selectedCountryCode
                    navController.navigate(AppDestination.CountrySelect.createRoute(currentCode))
                },
                onLanguageClick = {
                    val currentLanguage = discoverViewModel.uiState.value.selectedLanguage
                    navController.navigate(AppDestination.LanguageSelect.createRoute(currentLanguage))
                },
                contentPadding = contentPadding
            )
        }
        composable(AppDestination.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onAboutClick = { navController.navigate(AppDestination.About.route) }
            )
        }
        composable(AppDestination.About.route) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = AppDestination.CountrySelect.route,
            arguments = listOf(
                navArgument("selectedCode") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val selectedCode = backStackEntry.arguments?.getString("selectedCode")
            CountrySelectScreen(
                selectedCountryCode = selectedCode,
                onCountrySelected = { country ->
                    discoverViewModel.updateCountry(country.isoCode)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = AppDestination.LanguageSelect.route,
            arguments = listOf(
                navArgument("selectedLanguage") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val selectedLanguage = backStackEntry.arguments?.getString("selectedLanguage")
            LanguageSelectScreen(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { language ->
                    discoverViewModel.updateLanguage(language.name)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
