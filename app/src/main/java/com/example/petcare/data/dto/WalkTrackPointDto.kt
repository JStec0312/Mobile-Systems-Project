package com.example.petcare.data.dto

import com.google.firebase.Timestamp

data class WalkTrackPointDto(
    val walk_id: String,             // subkolekcja pod walks/{walkId}/track => pole opcjonalne
    val ts: Timestamp,
    val lat: Double,
    val lon: Double,
<<<<<<< Updated upstream
    val altitude: Double? = null
=======
>>>>>>> Stashed changes
)
