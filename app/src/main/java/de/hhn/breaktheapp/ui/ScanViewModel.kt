package de.hhn.breaktheapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.hhn.breaktheapp.data.LocationRepository
import de.hhn.breaktheapp.data.hasLocationPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LocationRepository(application)

    private val _state = MutableStateFlow<LocationState>(LocationState.Idle)
    val state: StateFlow<LocationState> = _state.asStateFlow()

    fun onPermissionDenied() {
        _state.value = LocationState.Error("Ohne Standortrecht kann die App deinen Standort nicht anzeigen.")
    }

    fun loadLocation() {
        if (!hasLocationPermission(getApplication())) {
            onPermissionDenied()
            return
        }
        _state.value = LocationState.Loading
        viewModelScope.launch {
            val loc = repository.getLocation()
            _state.value = LocationState.Success(loc.latitude, loc.longitude, loc.accuracy)
        }
    }
}
