package com.armanmaurya.internetradio.ui.screens.added

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armanmaurya.internetradio.data.model.RadioStation
import com.armanmaurya.internetradio.data.repository.UserStationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddedViewModel @Inject constructor(
    private val repository: UserStationRepository
) : ViewModel() {

    val userStations: StateFlow<List<RadioStation>> = repository.getAllUserStations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addStation(name: String, url: String, favicon: String) {
        viewModelScope.launch {
            repository.addUserStation(name, url, favicon)
        }
    }

    fun deleteStation(stationUuid: String) {
        viewModelScope.launch {
            repository.deleteUserStation(stationUuid)
        }
    }
}
