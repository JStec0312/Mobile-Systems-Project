package com.example.petcare.domain.model

import java.time.Instant
import java.util.UUID

data class Walk(
    val id: UUID,
    val pet_id: UUID,
    val started_at: Instant,
    val ended_at: Instant?,
    val duration_sec: Int?,
    val distance_meters: Int?,
    val steps: Int?,
    val created_at: Instant
)