package com.example.petcare.domain.model

import java.time.Instant
import java.util.UUID

data class PetMember(
    val pet_id: UUID,
    val user_id: UUID,
    val created_at: Instant
)