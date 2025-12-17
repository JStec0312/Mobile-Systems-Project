package com.example.petcare.data.dto.fake

data class WalkDto(
    val id: String,
    val petId: String,                  // subkolekcja pod pets/{petId}/walks => pole opcjonalne
    val startedAt: String,
    var endedAt: String? = null,
    var durationSec: Int? = null,
    var distanceMeters: Int? = null,
    var steps: Int? = null,
    var pending: Boolean,
    val createdAt: String,
)

