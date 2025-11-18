package com.example.petcare.data.dto

import com.example.petcare.domain.model.WalkTrackPoint
import com.google.firebase.Timestamp
import kotlinx.datetime.Instant

data class WalkTrackPointDto(
    val id: String,
    val walkId: String,             // subkolekcja pod walks/{walkId}/track => pole opcjonalne
    val ts: Instant,
    val lat: Double,
    val lon: Double,
    val altitude: Double
) {
    fun toModel(): WalkTrackPoint{
        return WalkTrackPoint(
            id = this.id,
            walkId = this.walkId,
            ts = this.ts,
            lat = this.lat,
            lon = this.lon,
            altitude = this.altitude
        )
    }
}

