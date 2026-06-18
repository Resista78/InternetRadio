package com.armanmaurya.internetradio.ui.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armanmaurya.internetradio.data.model.RadioStation
import com.armanmaurya.internetradio.data.repository.SettingsRepository
import com.armanmaurya.internetradio.data.repository.StationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiscoverUiState(
    val searchQuery: String = "",
    val stations: List<RadioStation> = emptyList(),
    val isLoading: Boolean = false,
    val isNextPageLoading: Boolean = false,
    val canLoadMore: Boolean = true,
    val isSearchActive: Boolean = false,
    val isSearchExpanded: Boolean = false,
    val error: String? = null,
    val selectedCountryCode: String? = null,
    val selectedLanguage: String? = null,
    val selectedTags: Set<String> = emptySet(),
    val order: String = "votes",
    val reverse: Boolean = true,
)

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: StationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private var currentOffset = 0
    private val pageSize = 60

    init {
        observeSettings()
        observeSearchQuery()
    }

    private fun observeSettings() {
        settingsRepository.appPreferencesFlow
            .take(1)
            .onEach { preferences ->
                _uiState.update { 
                    it.copy(
                        selectedCountryCode = preferences.selectedCountryCode,
                        selectedLanguage = preferences.selectedLanguage,
                        selectedTags = preferences.selectedTags,
                        order = preferences.order,
                        reverse = preferences.reverse
                    )
                }
                if (preferences.selectedCountryCode == null) {
                    detectCountryIfNeeded()
                } else {
                    loadStations(
                        countryCode = preferences.selectedCountryCode, 
                        language = preferences.selectedLanguage,
                        tags = preferences.selectedTags
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun detectCountryIfNeeded() {
        viewModelScope.launch {
            if (_uiState.value.selectedCountryCode == null) {
                _uiState.update { it.copy(isLoading = true) }
                repository.getCurrentCountryCode()
                    .onSuccess { countryCode ->
                        updateCountry(countryCode)
                    }
                    .onFailure {
                        _uiState.update { it.copy(isLoading = false, selectedCountryCode = null) }
                        loadStations(null)
                    }
            }
        }
    }

    fun updateCountry(countryCode: String) {
        if (_uiState.value.selectedCountryCode == countryCode) return
        
        _uiState.update { it.copy(selectedCountryCode = countryCode) }
        
        viewModelScope.launch {
            settingsRepository.setSelectedCountryCode(countryCode)
        }
        
        reloadStations()
    }

    fun updateLanguage(language: String) {
        val normalizedLanguage = if (language == "All Languages") null else language
        if (_uiState.value.selectedLanguage == normalizedLanguage) return
        
        _uiState.update { it.copy(selectedLanguage = normalizedLanguage) }
        
        viewModelScope.launch {
            settingsRepository.setSelectedLanguage(normalizedLanguage)
        }
        
        reloadStations()
    }

    fun updateTags(tags: Set<String>) {
        if (_uiState.value.selectedTags == tags) return
        
        _uiState.update { it.copy(selectedTags = tags) }
        
        viewModelScope.launch {
            settingsRepository.setSelectedTags(tags)
        }
        
        reloadStations()
    }

    private fun loadStations(
        countryCode: String?, 
        language: String? = _uiState.value.selectedLanguage,
        tags: Set<String> = _uiState.value.selectedTags
    ) {
        viewModelScope.launch {
            currentOffset = 0
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    error = null, 
                    selectedCountryCode = countryCode,
                    selectedLanguage = language,
                    selectedTags = tags,
                    canLoadMore = true
                ) 
            }
            val state = _uiState.value
            val result = repository.filterStations(
                countryCode = countryCode?.takeIf { it.isNotBlank() },
                language = language?.takeIf { it.isNotBlank() },
                tagList = tags.joinToString(",").takeIf { it.isNotBlank() },
                order = state.order,
                reverse = state.reverse,
                limit = pageSize,
                offset = currentOffset
            )

            result.onSuccess { stations ->
                _uiState.update { it.copy(stations = stations.distinctBy { it.stationUuid }, isLoading = false, canLoadMore = stations.size >= pageSize) }
            }
            .onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    fun loadMoreStations() {
        var shouldProceed = false
        _uiState.update { 
            if (!it.isLoading && !it.isNextPageLoading && it.canLoadMore) {
                shouldProceed = true
                it.copy(isNextPageLoading = true)
            } else {
                it
            }
        }

        if (!shouldProceed) return

        viewModelScope.launch {
            val state = _uiState.value
            currentOffset += pageSize
            
            val result = if (state.searchQuery.isBlank()) {
                repository.filterStations(
                    countryCode = state.selectedCountryCode?.takeIf { it.isNotBlank() },
                    language = state.selectedLanguage?.takeIf { it.isNotBlank() },
                    tagList = state.selectedTags.joinToString(",").takeIf { it.isNotBlank() },
                    order = state.order,
                    reverse = state.reverse,
                    limit = pageSize,
                    offset = currentOffset
                )
            } else {
                repository.filterStations(
                    name = state.searchQuery,
                    language = state.selectedLanguage?.takeIf { it.isNotBlank() },
                    tagList = state.selectedTags.joinToString(",").takeIf { it.isNotBlank() },
                    order = state.order,
                    reverse = state.reverse,
                    limit = pageSize,
                    offset = currentOffset
                )
            }

            result.onSuccess { newStations ->
                _uiState.update { 
                    it.copy(
                        stations = (it.stations + newStations).distinctBy { station -> station.stationUuid },
                        isNextPageLoading = false,
                        canLoadMore = newStations.size >= pageSize
                    )
                }
            }
            .onFailure {
                _uiState.update { it.copy(isNextPageLoading = false) }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _uiState
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(400)
            .onEach { query ->
                if (query.isBlank()) {
                    loadStations(_uiState.value.selectedCountryCode)
                } else {
                    searchStations(query)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun searchStations(query: String) {
        viewModelScope.launch {
            currentOffset = 0
            _uiState.update { it.copy(isLoading = true, error = null, canLoadMore = true) }
            val state = _uiState.value
            repository.filterStations(
                name = query,
                language = state.selectedLanguage?.takeIf { it.isNotBlank() },
                tagList = state.selectedTags.joinToString(",").takeIf { it.isNotBlank() },
                order = state.order,
                reverse = state.reverse,
                limit = pageSize,
                offset = currentOffset
            )
                .onSuccess { stations ->
                    _uiState.update { it.copy(stations = stations.distinctBy { it.stationUuid }, isLoading = false, canLoadMore = stations.size >= pageSize) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun onOrderChange(order: String) {
        if (_uiState.value.order == order) return
        _uiState.update { it.copy(order = order) }
        viewModelScope.launch {
            settingsRepository.setSortOrder(order)
        }
        reloadStations()
    }

    fun onReverseChange(reverse: Boolean) {
        if (_uiState.value.reverse == reverse) return
        _uiState.update { it.copy(reverse = reverse) }
        viewModelScope.launch {
            settingsRepository.setSortReverse(reverse)
        }
        reloadStations()
    }

    private fun reloadStations() {
        val state = _uiState.value
        if (state.searchQuery.isBlank()) {
            loadStations(state.selectedCountryCode)
        } else {
            searchStations(state.searchQuery)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                isSearchActive = query.isNotBlank(),
            )
        }
    }

    fun onSearchExpandedChange(expanded: Boolean) {
        _uiState.update {
            it.copy(isSearchExpanded = expanded)
        }
    }

    fun onSearchCleared() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                isSearchActive = false,
                isSearchExpanded = false,
            )
        }
    }
}
