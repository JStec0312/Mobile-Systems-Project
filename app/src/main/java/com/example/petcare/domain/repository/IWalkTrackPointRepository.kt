package com.example.petcare.domain.repository

import com.example.petcare.domain.model.WalkTrackPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface IWalkTrackPointRepository {
    suspend fun addWalkTrackPoint(walkTrackPoint: WalkTrackPoint)
    fun observeWalkPoints(walkId: String): Flow<List<WalkTrackPoint>>

}