package com.example.petcare.data.dto.firestore

import com.google.firebase.Timestamp

data class MedicationFirestoreDto(
    val id: String = "",
    val petId: String = "",
    val name: String = "",
    val form: String? = null,
    val dose: String? = null,
    val notes: String? = null,
    val active: Boolean = true,
    val createdAt: Timestamp = Timestamp.now(),
    val from: Timestamp = Timestamp.now(),
    val to: Timestamp = Timestamp.now(),
    val reccurenceString: String = "",
    val times: List<String> = emptyList(),
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_PET_ID = "petId"
        const val FIELD_CREATED_AT = "createdAt"
    }
}
