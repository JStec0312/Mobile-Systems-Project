package com.example.petcare.domain.model

import com.example.petcare.common.taskStatusEnum
import java.time.Instant
import java.util.UUID

data class MedicationEvent(
    val id: String,
    val medicationId: String,
    val takenAt: Instant?,
    val status: taskStatusEnum = taskStatusEnum.planned, // planned|done|skipped
    val notes: String?
)
