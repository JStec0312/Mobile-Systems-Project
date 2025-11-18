package com.example.petcare.domain.model

import java.time.Instant
import java.util.UUID

data class PetShareCode(
    val id: String,
    val petId: String,
    val code: String,
    val createdAt: Instant
)
