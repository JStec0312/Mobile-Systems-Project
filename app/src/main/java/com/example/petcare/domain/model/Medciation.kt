package com.example.petcare.domain.model
import kotlinx.datetime.Instant
import java.util.UUID

data class Medication(
    val id: String,
    val petId: String,
    val name: String,
    val form: String?,
    val dose: String?,
    val notes: String?,
    val active: Boolean = true,
    val createdAt: Instant,
    val from: Instant,
    val to: Instant?
)