package com.example.petcare.domain.model

import java.time.Instant
import java.util.UUID

data class PetMember(
    val petId: UUID,
    val userId: UUID,
    val createdAt: Instant
)