package com.example.petcare.domain.model

import com.example.petcare.common.medicationStatusEnum
import com.example.petcare.common.taskStatusEnum
import kotlinx.datetime.LocalDate

data class MedicationEvent(
    val id: String,
    val medicationId: String,
    val takenAt: LocalDate,
    val status: medicationStatusEnum= medicationStatusEnum.planned, // planned|done|skipped
    val notes: String?,
    val scheduledAt: LocalDate,
)
