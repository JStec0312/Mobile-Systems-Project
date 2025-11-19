package com.example.petcare.domain.model

import com.example.petcare.data.dto.WalkDto
import kotlinx.datetime.LocalDate

data class Walk(
    val id: String,
    val petId: String,
    val startedAt: LocalDate,
    val endedAt: LocalDate,
    val durationSec: Int?,
    val distanceMeters: Int?,
    val steps: Int?,
    val createdAt: LocalDate
) {
    fun toDto(): WalkDto{
        return WalkDto(
            id = this.id,
            petId = this.petId,
            startedAt = this.startedAt.toString(),
            endedAt = this.endedAt.toString(),
            durationSec = this.durationSec,
            distanceMeters = this.distanceMeters,
            steps = this.steps,
            createdAt = this.createdAt.toString()
        )
    }
}