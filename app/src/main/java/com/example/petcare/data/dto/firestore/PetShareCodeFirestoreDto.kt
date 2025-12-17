package com.example.petcare.data.dto.firestore

import com.google.firebase.Timestamp

data class PetShareCodeFirestoreDto(
    val id: String = "",
    val petId: String = "",
    val code: String = "",
    val expiresAt: Timestamp = Timestamp.now(),
    val createdAt: Timestamp? = null,
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_PET_ID = "petId"
        const val FIELD_EXPIRES_AT = "expiresAt"
    }
}
