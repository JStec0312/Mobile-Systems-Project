package com.example.petcare.data.dto
import com.google.firebase.Timestamp
data class WalkDto(
    val pet_id: String,                  // subkolekcja pod pets/{petId}/walks => pole opcjonalne
    val started_at: Timestamp,
    val ended_at: Timestamp? = null,
    val duration_sec: Int? = null,
    val distance_meters: Int? = null,
    val steps: Int? = null,
    val created_at: Timestamp? = null
)