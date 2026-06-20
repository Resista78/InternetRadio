package com.armanmaurya.internetradio.ui.screens.selectcountry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armanmaurya.internetradio.data.model.Country
import com.armanmaurya.internetradio.data.repository.StationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CountrySelectUiState(
    val countries: List<Country> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)

@HiltViewModel
class CountrySelectViewModel @Inject constructor(
    private val repository: StationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CountrySelectUiState())
    val uiState: StateFlow<CountrySelectUiState> = _uiState.asStateFlow()

    init {
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getCountries()
                .onSuccess { countries ->
                    _uiState.update { it.copy(countries = countries, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSearch() {
        _uiState.update { 
            val newActive = !it.isSearchActive
            it.copy(
                isSearchActive = newActive,
                searchQuery = if (newActive) it.searchQuery else ""
            )
        }
    }
}
