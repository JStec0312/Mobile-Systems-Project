package com.example.petcare.domain.model

import kotlinx.datetime.LocalDate

data class Walk(
    val id: String,
    val petId: String,
    val startedAt: LocalDate,
    val endedAt: LocalDate,
    val durationSec: LocalDate,
    val distanceMeters: Int?,
    val steps: Int?,
    val createdAt: LocalDate
)