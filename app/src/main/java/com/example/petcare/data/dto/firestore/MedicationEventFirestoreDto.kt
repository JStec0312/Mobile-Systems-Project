package com.example.petcare.data.dto.firestore

import com.example.petcare.common.medicationStatusEnum
import com.google.firebase.Timestamp

data class MedicationEventFirestoreDto(
    val id: String = "",
    val petId: String = "",
    val title: String = "",
    val medicationId: String = "",
    val takenAt: Timestamp? = null,
    val status: medicationStatusEnum = medicationStatusEnum.planned,
    val notes: String? = null,
    val scheduledAt: Timestamp? = null,
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_PET_ID = "petId"
        const val FIELD_MEDICATION_ID = "medicationId"
        const val FIELD_TAKEN_AT = "takenAt"
        const val FIELD_SCHEDULED_AT = "scheduledAt"
    }
}
