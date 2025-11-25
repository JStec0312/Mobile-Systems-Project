package com.example.petcare.domain.model

import com.example.petcare.common.taskStatusEnum
import kotlinx.datetime.LocalDate

data class MedicationEvent(
    val id: String,
    val medicationId: String,
    val takenAt: LocalDate,
    val status: taskStatusEnum = taskStatusEnum.planned, // planned|done|skipped
    val notes: String?
)
