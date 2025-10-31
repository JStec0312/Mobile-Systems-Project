package com.example.petcare.domain.model

import java.time.Instant
import java.util.UUID

data class PetShareCode(
    val id: UUID,
    val pet_id: UUID,
    val code: String,
    val created_at: Instant
)
