package com.example.petcare.domain.model

import kotlinx.datetime.LocalDate

data class Walk(
    val id: String,
    val petId: String,
    val startedAt: LocalDate,
    var endedAt: LocalDate?,
    val durationSec: Int?,
    val distanceMeters: Int?,
    val steps: Int?,
    val pending: Boolean,
    val createdAt: LocalDate
)
