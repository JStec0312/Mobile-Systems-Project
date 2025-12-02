package com.example.petcare.presentation.walk

import com.example.petcare.domain.model.Walk
import com.google.android.gms.maps.model.LatLng


data class WalkState(
    val timerValue: String = "00:00",
    val distanceValue: String = "0.0 km",
    val stepsValue: String = "0",
    val routePoints: List<LatLng> = emptyList(),
    val isLocationEnabled: Boolean = false,
    val currentLocation: LatLng? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)


