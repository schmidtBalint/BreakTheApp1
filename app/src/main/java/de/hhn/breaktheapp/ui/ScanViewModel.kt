package de.hhn.breaktheapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.hhn.breaktheapp.data.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LocationRepository(application)

    private val _state = MutableStateFlow<LocationState>(LocationState.Idle)
    val state: StateFlow<LocationState> = _state.asStateFlow()

    fun loadLocation() {
        _state.value = LocationState.Loading
        viewModelScope.launch {
            val loc = repository.getLocation()
            _state.value = LocationState.Success(loc.latitude, loc.longitude, loc.accuracy)
        }
    }
}
