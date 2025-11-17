package com.example.petcare.domain.model

import java.time.Instant
import java.util.UUID

data class WalkTrackPointDto(
    val id: UUID,
    val walkId: UUID,
    val timestamp: Instant,
    val lat: Float,
    val lon: Float,
    val altitude: Float?
)
