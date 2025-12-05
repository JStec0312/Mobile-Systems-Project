package com.example.petcare.data.fake_repos

import com.example.petcare.data.dto.WalkTrackPointDto
import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FakeWalkTrackPointRepository @Inject constructor() : IWalkTrackPointRepository {

    // Używamy StateFlow do symulowania bazy danych w pamięci
    private val _pointsFlow = MutableStateFlow<List<WalkTrackPoint>>(emptyList())
    override suspend fun addWalkTrackPoint(walkTrackPoint: WalkTrackPoint) {
        val currentList = _pointsFlow.value.toMutableList()
        currentList.add(walkTrackPoint)
        _pointsFlow.emit(currentList)    }


    override  fun observeWalkPoints(walkId: String): Flow<List<WalkTrackPoint>> {
        return _pointsFlow.map { list ->
            list.filter { it.walkId == walkId }
        }
    }
}