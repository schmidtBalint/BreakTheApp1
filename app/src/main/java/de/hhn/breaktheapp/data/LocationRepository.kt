package de.hhn.breaktheapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class LocationRepository(context: Context) {

    private val fused = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager = context.getSystemService(LocationManager::class.java)

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Location {
        // Auf die Standortdienste warten
        while (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            delay(500)
        }
        return fused.lastLocation.await()!!
    }
}
