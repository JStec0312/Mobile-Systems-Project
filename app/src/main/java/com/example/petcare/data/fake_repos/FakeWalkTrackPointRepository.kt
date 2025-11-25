package com.example.petcare.data.fake_repos

import com.example.petcare.data.dto.WalkTrackPointDto
import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.repository.IWalkTrackPointRepository

class FakeWalkTrackPointRepository : IWalkTrackPointRepository {
    private val walkTrackPoints = mutableListOf<WalkTrackPointDto>()
    override fun addWalkTrackPoint(walkTrackPoint: WalkTrackPoint) {
        val walkTrackPointDto = walkTrackPoint.toDto()
        walkTrackPoints.add(walkTrackPointDto)
    }
}