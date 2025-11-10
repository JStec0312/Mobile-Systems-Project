package com.example.petcare.domain.model

import com.example.petcare.common.taskStatusEnum
import java.time.Instant
import java.util.UUID

data class MedicationEvent(
    val id: UUID,
    val medication_id: UUID,
    val taken_at: Instant?,
    val status: taskStatusEnum = taskStatusEnum.planned, // planned|done|skipped
    val notes: String?
)
