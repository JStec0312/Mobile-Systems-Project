package com.example.petcare.domain.model

import java.time.Instant
import java.util.UUID

data class PetMember(
    val petId: String,
    val userId: String,
    val createdAt: Instant
)