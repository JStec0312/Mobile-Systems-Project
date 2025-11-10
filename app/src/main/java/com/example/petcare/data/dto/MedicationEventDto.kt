package com.example.petcare.data.dto
import com.example.petcare.common.taskStatusEnum
import com.google.firebase.Timestamp

data class MedicationEventDto(
    val medication_id: String,        // subkolekcja pod medications/{medId}/events => pole opcjonalne
    val taken_at: Timestamp? = null,
    val status: taskStatusEnum = taskStatusEnum.planned, // planned|done|skipped|cancelled
    val notes: String? = null
)