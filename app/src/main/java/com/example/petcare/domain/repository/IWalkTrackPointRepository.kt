package com.example.petcare.domain.repository

import com.example.petcare.domain.model.WalkTrackPoint

interface IWalkTrackPointRepository {
    fun addWalkTrackPoint(walkTrackPoint: WalkTrackPoint)
}