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

    fun onPermissionDenied(permanently: Boolean = false) {
        _state.value = if (permanently) {
            LocationState.Error(
                "Das Standortrecht ist dauerhaft verweigert. Du kannst es in den Einstellungen erlauben.",
                showSettings = true
            )
        } else {
            LocationState.Error("Ohne Standortrecht kann die App deinen Standort nicht anzeigen.")
        }
    }

    fun loadLocation() {
        if (!hasLocationPermission(getApplication())) {
            onPermissionDenied()
            return
        }
        _state.value = LocationState.Loading
        viewModelScope.launch {
            _state.value = repository.getLocation()
        }
    }
}
