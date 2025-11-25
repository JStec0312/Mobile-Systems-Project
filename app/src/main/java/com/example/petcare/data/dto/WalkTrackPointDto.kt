package com.example.petcare.data.dto

import kotlinx.datetime.Instant

data class WalkTrackPointDto(
    val id: String,
    val walkId: String,             // subkolekcja pod walks/{walkId}/track => pole opcjonalne
    val ts: Instant,
    val lat: Double,
    val lon: Double,
    val altitude: Double
)

