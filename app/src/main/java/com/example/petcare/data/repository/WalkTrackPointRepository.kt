package com.example.petcare.data.repository

import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import kotlinx.coroutines.flow.Flow

class WalkTrackPointRepository : IWalkTrackPointRepository {
    override suspend fun addWalkTrackPoint(walkTrackPoint: WalkTrackPoint) {
        TODO("Not yet implemented")
    }

    override  fun observeWalkPoints(walkId: String): Flow<List<WalkTrackPoint>> {
        TODO("Not yet implemented")
    }

}