package com.example.petcare.domain.use_case.process_location_update

import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class ProcessLocationUpdateUseCase @Inject constructor(
    private val walkTrackPointRepository: IWalkTrackPointRepository
) {
    suspend operator fun invoke(walkId: String, latitude: Double, longitude: Double) {
        val newPoint: WalkTrackPoint = WalkTrackPoint(
            id = UUID.randomUUID().toString(),
            walkId = walkId,
            ts = Clock.System.now(),
            lat = latitude,
            lon = longitude,
        )
        walkTrackPointRepository.addWalkTrackPoint(newPoint)

    }
}