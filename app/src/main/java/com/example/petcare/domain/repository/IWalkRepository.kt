package com.example.petcare.domain.repository

import com.example.petcare.domain.model.Walk
import com.example.petcare.exceptions.Failure
import kotlin.jvm.Throws

interface IWalkRepository {
    @Throws (Failure.ServerError::class, Failure.NetworkError::class, Failure.UnknownError::class )
    fun createWalk(walk: Walk);

}