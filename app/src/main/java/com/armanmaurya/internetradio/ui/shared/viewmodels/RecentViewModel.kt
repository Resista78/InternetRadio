package com.armanmaurya.internetradio.ui.shared.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armanmaurya.internetradio.data.model.RadioStation
import com.armanmaurya.internetradio.data.repository.FavoriteRepository
import com.armanmaurya.internetradio.data.repository.RecentRepository
import com.armanmaurya.internetradio.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    private val recentRepository: RecentRepository,
    private val settingsRepository: SettingsRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val useFilter: StateFlow<Boolean> = settingsRepository.appPreferencesFlow
        .map { it.useFilterOnRecent }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isGridView: StateFlow<Boolean> = settingsRepository.appPreferencesFlow
        .map { it.isGridViewRecent }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val favoriteStationUuids: StateFlow<Set<String>> = favoriteRepository.getAllFavorites()
        .map { favorites -> favorites.map { it.stationUuid }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val recentStations: StateFlow<List<RadioStation>> = combine(
        recentRepository.getAllRecent(),
        settingsRepository.appPreferencesFlow
    ) { stations, preferences ->
        if (preferences.useFilterOnRecent) {
            val hasCountryFilter = !preferences.selectedCountryCode.isNullOrBlank()
            val hasLanguageFilter = !preferences.selectedLanguage.isNullOrBlank()
            val hasTagFilter = preferences.selectedTags.isNotEmpty()

            // If no filter criteria are set at all, show everything
            if (!hasCountryFilter && !hasLanguageFilter && !hasTagFilter) {
                stations
            } else {
                stations.filter { station ->
                    val countryMatch = !hasCountryFilter ||
                            station.countryCode == preferences.selectedCountryCode
                    val languageMatch = !hasLanguageFilter ||
                            station.language == preferences.selectedLanguage
                    val tagsMatch = !hasTagFilter ||
                            preferences.selectedTags.any { it in station.tags }

                    countryMatch && languageMatch && tagsMatch
                }
            }
        } else {
            stations
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleFilter() {
        viewModelScope.launch {
            settingsRepository.setUseFilterOnRecent(!useFilter.value)
        }
    }

    fun onGridViewChange(isGrid: Boolean) {
        viewModelScope.launch { settingsRepository.setGridViewRecent(isGrid) }
    }
}