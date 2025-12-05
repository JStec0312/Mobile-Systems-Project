package com.example.petcare.domain.device_api

import com.example.petcare.domain.model.WalkTrackPoint
import kotlinx.coroutines.flow.Flow

interface ILocationClient {
    fun getLocationUpdates(intervalMs: Long): Flow<WalkTrackPoint>
}