package com.example.petcare.domain.device_api

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface IWalkTracker {
    fun getLocationUpdates(intervalMillis: Long): Flow<Location>
}