package com.example.petcare.domain.model

import kotlinx.datetime.Instant

data class WalkTrackPoint(
    val id: String,
    val walkId: String,
    val ts: Instant,
    val lat: Double,
    val lon: Double,
    val altitude: Double,
)