package com.armanmaurya.internetradio.ui.tv.navigation

sealed class AppDestination(val route: String) {
    data object Browse : AppDestination("browse")
    data object Recent : AppDestination("recent")
    data object Favorites : AppDestination("favorites")
    data object Added : AppDestination("added")
    data object Settings : AppDestination("settings")
    data object Player : AppDestination("player")
    data object Tags : AppDestination("tags")
    data object Language : AppDestination("language")
    data object Country : AppDestination("country")
}
