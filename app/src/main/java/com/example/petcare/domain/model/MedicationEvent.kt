package com.example.petcare.domain.model

import com.example.petcare.common.medicationStatusEnum
import com.example.petcare.common.taskStatusEnum
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class MedicationEvent(
    val id: String,
    val petId: String,
    val title: String,
    val medicationId: String,
    val takenAt: Instant,
    val status: medicationStatusEnum= medicationStatusEnum.planned, // planned|done|skipped
    val notes: String?,
    val scheduledAt: Instant,
)
