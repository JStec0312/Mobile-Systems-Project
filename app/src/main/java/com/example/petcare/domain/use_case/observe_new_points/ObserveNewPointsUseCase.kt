package com.example.petcare.domain.use_case.observe_new_points

import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

class ObserveNewPointsUseCase @Inject constructor(
    private val trackPointRepository: IWalkTrackPointRepository
) {
    operator fun invoke(walkId: String): Flow<WalkTrackPoint> {
        return trackPointRepository.newPointStream
            .filter { it.walkId == walkId }
    }
}