package de.hhn.breaktheapp.ui

sealed interface LocationState {
    data object Idle : LocationState
    data object Loading : LocationState
    data class Success(val lat: Double, val lon: Double, val accuracy: Float) : LocationState
}
