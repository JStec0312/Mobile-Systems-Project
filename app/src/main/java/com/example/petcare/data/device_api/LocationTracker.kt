package com.example.petcare.data.device_api

import android.annotation.SuppressLint
import android.content.Context
import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.exceptions.PermissionFailure
import com.example.petcare.service.interfaces.ILocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: FusedLocationProviderClient // FusedLocationProviderClient takes user Location from either GPS or Network
) : ILocationTracker {
    @SuppressLint("MissingPermission")
    override fun getLocationFlow(intervalMs: Long, walkId: String): Flow<WalkTrackPoint> {
        return callbackFlow {
            if (!hasLocationPermission(context)) {
                close(PermissionFailure.LocationPermissionDenied("Location permission not granted"))
                return@callbackFlow
            }
            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                intervalMs
            ).apply {
                setMinUpdateDistanceMeters(2f)
                setWaitForAccurateLocation(false)
            }.build()
            val locationCallback = object: LocationCallback(){
                override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                    result.locations.lastOrNull()?.let{ location ->
                        val trackPoint = WalkTrackPoint(
                            id = "",
                            walkId = walkId,
                            ts = kotlinx.datetime.Clock.System.now(),
                            lat = location.latitude,
                            lon = location.longitude,
                            altitude = location.altitude
                        )
                        launch { send(trackPoint) }
                    }
                }
            }
            client.requestLocationUpdates(request, locationCallback, null)
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }

        }
    }

    private fun hasLocationPermission(context: Context): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

}