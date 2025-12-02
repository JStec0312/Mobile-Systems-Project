package com.example.petcare.domain.repository

import com.example.petcare.domain.model.WalkTrackPoint
import kotlinx.coroutines.flow.Flow

interface IWalkTrackPointRepository {
    suspend fun addWalkTrackPoint(walkTrackPoint: WalkTrackPoint)
    fun getTrackPointsForWalk(walkId: String): Flow<List<WalkTrackPoint>>
    val newPointStream: Flow<WalkTrackPoint>

}