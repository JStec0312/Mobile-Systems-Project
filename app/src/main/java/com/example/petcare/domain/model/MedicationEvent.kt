package com.example.petcare.domain.model

import com.example.petcare.common.taskStatusEnum
import com.example.petcare.data.dto.MedicationEventDto
import kotlinx.datetime.LocalDate

data class MedicationEvent(
    val id: String,
    val medicationId: String,
    val takenAt: LocalDate,
    val status: taskStatusEnum = taskStatusEnum.planned, // planned|done|skipped
    val notes: String?
){
    fun toDto(): MedicationEventDto{
        return MedicationEventDto(
            id = this.id,
            medicationId = this.medicationId,
            takenAt = this.takenAt.toString(),
            status = this.status,
            notes = this.notes
        )
    }
}
