package com.armanmaurya.internetradio.ui.navigation

sealed class AppDestination(val route: String) {
    data object Discover : AppDestination("discover")
    data object Settings : AppDestination("settings")
    data object CountrySelect : AppDestination("country_select?selectedCode={selectedCode}") {
        fun createRoute(selectedCode: String?) = "country_select?selectedCode=$selectedCode"
    }
    data object LanguageSelect : AppDestination("language_select?selectedLanguage={selectedLanguage}") {
        fun createRoute(selectedLanguage: String?) = "language_select?selectedLanguage=$selectedLanguage"
    }
}