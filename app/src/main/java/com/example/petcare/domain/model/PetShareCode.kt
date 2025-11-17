package com.example.petcare.domain.model

import java.time.Instant
import java.util.UUID

data class PetShareCode(
    val id: UUID,
    val petId: UUID,
    val code: String,
    val createdAt: Instant
)
