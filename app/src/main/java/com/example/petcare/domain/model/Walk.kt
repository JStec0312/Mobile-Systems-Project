package com.example.petcare.domain.model

import java.util.UUID

data class Walk(
    val id: UUID,
    val petId: UUID,
    val startedAt: kotlinx.datetime.Instant,
    val endedAt: kotlinx.datetime.Instant,
    val durationSec: Int?,
    val distanceMeters: Int?,
    val steps: Int?,
    val createdAt: kotlinx.datetime.Instant
)