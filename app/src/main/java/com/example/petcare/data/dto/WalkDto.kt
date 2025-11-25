package com.example.petcare.data.dto

data class WalkDto(
    val id: String,
    val petId: String,                  // subkolekcja pod pets/{petId}/walks => pole opcjonalne
    val startedAt: String,
    val endedAt: String? = null,
    val durationSec: Int? = null,
    val distanceMeters: Int? = null,
    val steps: Int? = null,
    val pending: Boolean,
    val createdAt: String,
)

