package com.example.petcare.domain.repository

import com.example.petcare.domain.model.Walk
import com.example.petcare.exceptions.Failure
import kotlinx.datetime.Instant
import kotlin.jvm.Throws

interface IWalkRepository {
    @Throws (Failure.ServerError::class, Failure.NetworkError::class, Failure.UnknownError::class )
    fun createWalk(walk: Walk);
    fun setWalkAsEnded(walkId: String, totalDistanceMeters: Float, totalSteps: Int, endTime: Instant);


}