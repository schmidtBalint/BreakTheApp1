package de.hhn.breaktheapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import de.hhn.breaktheapp.ui.LocationState
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class LocationRepository(context: Context) {

    private val fused = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager = context.getSystemService(LocationManager::class.java)

    @SuppressLint("MissingPermission") // wird vorher im ViewModel geprüft
    suspend fun getLocation(): LocationState {
        if (!LocationManagerCompat.isLocationEnabled(locationManager))
            return LocationState.Error("Standortdienste sind aus. Bitte in den Schnelleinstellungen einschalten.")
        val loc = withTimeoutOrNull(5_000) {
            fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
        }
        return loc?.let { LocationState.Success(it.latitude, it.longitude, it.accuracy) }
            ?: LocationState.Error("Kein Standort ermittelbar")
    }
}
