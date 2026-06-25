package com.armanmaurya.internetradio.ui.screens.home.tabs.added

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armanmaurya.internetradio.data.model.RadioStation
import com.armanmaurya.internetradio.data.repository.SettingsRepository
import com.armanmaurya.internetradio.data.repository.UserStationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddedViewModel @Inject constructor(
    private val repository: UserStationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val useFilter: StateFlow<Boolean> = settingsRepository.appPreferencesFlow
        .map { it.useFilterOnAdded }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isGridView: StateFlow<Boolean> = settingsRepository.appPreferencesFlow
        .map { it.isGridViewAdded }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val userStations: StateFlow<List<RadioStation>> = combine(
        repository.getAllUserStations(),
        settingsRepository.appPreferencesFlow
    ) { stations, preferences ->
        if (preferences.useFilterOnAdded) {
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

    fun addStation(
        name: String,
        url: String,
        favicon: String,
        tags: List<String> = emptyList(),
        country: String = "",
        countryCode: String = "",
        language: String = ""
    ) {
        viewModelScope.launch {
            repository.addUserStation(
                name = name,
                url = url,
                favicon = favicon,
                tags = tags,
                country = country,
                countryCode = countryCode,
                language = language
            )
        }
    }

    fun toggleFilter() {
        viewModelScope.launch {
            settingsRepository.setUseFilterOnAdded(!useFilter.value)
        }
    }

    fun deleteStation(stationUuid: String) {
        viewModelScope.launch {
            repository.deleteUserStation(stationUuid)
        }
    }

    fun onGridViewChange(isGrid: Boolean) {
        viewModelScope.launch { settingsRepository.setGridViewAdded(isGrid) }
    }
}
