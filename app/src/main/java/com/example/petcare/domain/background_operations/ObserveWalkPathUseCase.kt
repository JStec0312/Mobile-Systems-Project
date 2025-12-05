package com.example.petcare.domain.use_case.walk

import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWalkPathUseCase @Inject constructor(
    private val repository: IWalkTrackPointRepository
) {
    operator fun invoke(walkId: String): Flow<List<WalkTrackPoint>> {
        return repository.observeWalkPoints(walkId)
    }
}