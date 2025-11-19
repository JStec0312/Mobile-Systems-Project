package com.example.petcare.domain.model

import com.example.petcare.data.dto.WalkTrackPointDto
import kotlinx.datetime.Instant

data class WalkTrackPoint(
    val id: String,
    val walkId: String,
    val ts: Instant,
    val lat: Double,
    val lon: Double,
    val altitude: Double,
){
    fun toDto(): WalkTrackPointDto{
        return WalkTrackPointDto(
            id = this.id,
            walkId = this.walkId,
            ts = this.ts,
            lat = this.lat,
            lon = this.lon,
            altitude = this.altitude
        )
    }
}