package com.example.petcare.data.dto
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.MedicationEvent

data class MedicationEventDto(
    val id: String,
    val medicationId: String,        // subkolekcja pod medications/{medId}/events => pole opcjonalne
    val takenAt: String?  = null,
    val status: taskStatusEnum = taskStatusEnum.planned, // planned|done|skipped|cancelled
    val notes: String? = null
) {
    fun toModel(): MedicationEvent{
        return MedicationEvent(
            id = this.id,
            medicationId = this.medicationId,
            takenAt = DateConverter.stringToLocalDate(this.takenAt),
            status = this.status,
            notes = this.notes
        )
    }
}