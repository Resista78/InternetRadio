package com.armanmaurya.internetradio.ui.screens.country

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armanmaurya.internetradio.data.model.Language
import com.armanmaurya.internetradio.data.repository.StationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LanguageSelectUiState(
    val languages: List<Language> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)

@HiltViewModel
class LanguageSelectViewModel @Inject constructor(
    private val repository: StationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageSelectUiState())
    val uiState: StateFlow<LanguageSelectUiState> = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getLanguages()
                .onSuccess { languages ->
                    _uiState.update { it.copy(languages = languages, isLoading = false) }
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
