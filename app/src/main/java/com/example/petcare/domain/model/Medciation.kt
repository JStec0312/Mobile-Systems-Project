package com.example.petcare.domain.model
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class Medication(
    val id: String,
    val petId: String,
    val name: String,
    val form: String?,
    val dose: String?,
    val notes: String?,
    val active: Boolean = true,
    val createdAt: LocalDate,
    val from: LocalDate,
    val to: LocalDate?,
    val reccurenceString: String,
    val times: List<Instant>
)
