package com.example.petcare.data.device_api

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.example.petcare.config.Settings
import com.example.petcare.domain.device_api.ILocationClient
import com.example.petcare.domain.model.WalkTrackPoint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class LocationClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: FusedLocationProviderClient
) : ILocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(intervalMs: Long): Flow<WalkTrackPoint> {
        return callbackFlow {

            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                intervalMs
            )
                .setMinUpdateDistanceMeters(Settings.WALK_MIN_UPDATE_DISTANCE_METERS)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)

                    result.locations.lastOrNull()?.let { location ->
                        launch {
                            send(
                                WalkTrackPoint(
                                    id = UUID.randomUUID().toString(),
                                    ts = Clock.System.now(),
                                    lat = location.latitude,
                                    lon = location.longitude,
                                    walkId = null, // ustawiamy w serwisie
                                )
                            )
                        }
                    }
                }
            }
            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}
