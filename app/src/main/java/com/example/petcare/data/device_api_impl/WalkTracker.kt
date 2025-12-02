package com.example.petcare.data.device_api_impl

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.petcare.domain.device_api.IWalkTracker
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class WalkTracker @Inject constructor(
    @ApplicationContext private val context: Context
) : IWalkTracker {
    private val client: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(intervalMillis: Long): Flow<Location> {
        return callbackFlow {
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis)
                .setMinUpdateIntervalMillis(2L)
                .build()
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.locations.lastOrNull()?.let { location -> launch { send(location) } }
                }
            };

        try {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (e: Exception) {
            close(e)
        }
            awaitClose {
                client.removeLocationUpdates(callback)
            }
        }

    }
}