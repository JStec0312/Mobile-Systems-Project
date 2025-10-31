package com.example.petcare.domain.model

import java.time.Instant
import java.util.UUID

data class Medication(
    val id: UUID,
    val pet_id: UUID,
    val name: String,
    val form: String?,
    val dose: String?,
    val notes: String?,
    val active: Boolean = true,
    val created_at: Instant
)