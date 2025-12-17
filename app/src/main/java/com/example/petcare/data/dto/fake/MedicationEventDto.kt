package com.example.petcare.data.dto.fake
import com.example.petcare.common.medicationStatusEnum

data class MedicationEventDto(
    val id: String,
    val petId : String,
    val title: String,
    val medicationId: String,        // subkolekcja pod medications/{medId}/events => pole opcjonalne
    val takenAt: String? = null,
    val status: medicationStatusEnum = medicationStatusEnum.planned, // planned|done|skipped|cancelled
    val notes: String? = null,
    val scheduledAt: String? = null,
)
