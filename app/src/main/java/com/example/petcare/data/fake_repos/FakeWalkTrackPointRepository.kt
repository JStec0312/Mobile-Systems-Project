package com.example.petcare.data.fake_repos

import com.example.petcare.data.dto.WalkTrackPointDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber

class FakeWalkTrackPointRepository : IWalkTrackPointRepository {
    private val walkTrackPoints = mutableListOf<WalkTrackPointDto>()
    private val _newPointsFlow = MutableSharedFlow<WalkTrackPoint>(replay = 0)
    override val newPointStream: Flow<WalkTrackPoint> = _newPointsFlow.asSharedFlow()

    override suspend fun addWalkTrackPoint(walkTrackPoint: WalkTrackPoint) {
        Timber.d("FakeWalkTrackPointRepository: Adding point - lat=${walkTrackPoint.lat}, lon=${walkTrackPoint.lon}, walkId=${walkTrackPoint.walkId}")
        walkTrackPoints.add(walkTrackPoint.toDto())
        // Emit the new point to the flow
        _newPointsFlow.emit(walkTrackPoint)
        Timber.d("FakeWalkTrackPointRepository: Point emitted successfully")
    }

    override fun getTrackPointsForWalk(walkId: String): Flow<List<WalkTrackPoint>> {
        val filteredPoints = walkTrackPoints
            .filter { it.walkId == walkId }
            .map { it.toDomain() }
        return kotlinx.coroutines.flow.flow {
            emit(filteredPoints)
        }
    }


}