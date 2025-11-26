package com.example.petcare.service.interfaces

import com.example.petcare.domain.model.WalkTrackPoint
import kotlinx.coroutines.flow.Flow

interface ILocationTracker {
    fun getLocationFlow(intervalMs: Long, walkId: String): Flow<WalkTrackPoint>
}