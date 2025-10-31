package com.example.petcare.domain.model

import java.time.Instant
import java.util.UUID

data class WalkTrackPointDto(
    val id: UUID,
    val walk_id: UUID,
    val ts: Instant,
    val lat: Float,
    val lon: Float,
    val altitude: Float?
)
