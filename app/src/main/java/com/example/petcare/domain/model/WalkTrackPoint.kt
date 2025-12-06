package com.example.petcare.domain.model

import kotlinx.datetime.Instant

data class WalkTrackPoint(
    val id: String,
    var walkId: String?,
    val ts: Instant,
    val lat: Double,
    val lon: Double,
)
