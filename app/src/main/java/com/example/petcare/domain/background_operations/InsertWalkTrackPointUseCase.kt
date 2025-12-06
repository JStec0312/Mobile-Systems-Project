package com.example.petcare.domain.background_operations

import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import javax.inject.Inject

class InsertWalkTrackPointUseCase @Inject constructor(
    private val walkTrackPointRepository: IWalkTrackPointRepository
) {
    suspend operator fun invoke(walkTrackPoint: WalkTrackPoint) {
        walkTrackPointRepository.addWalkTrackPoint(walkTrackPoint)
    }
}