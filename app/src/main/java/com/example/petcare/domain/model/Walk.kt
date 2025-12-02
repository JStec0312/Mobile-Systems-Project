package com.example.petcare.domain.model

<<<<<<< Updated upstream
import java.util.UUID

data class Walk(
    val id: UUID,
    val petId: UUID,
    val startedAt: kotlinx.datetime.Instant,
    val endedAt: kotlinx.datetime.Instant,
=======
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class Walk(
    val id: String,
    val petId: String,
    val startedAt: Instant,
    val endedAt: LocalDate?,
>>>>>>> Stashed changes
    val durationSec: Int?,
    val distanceMeters: Int?,
    val steps: Int?,
    val createdAt: kotlinx.datetime.Instant
)