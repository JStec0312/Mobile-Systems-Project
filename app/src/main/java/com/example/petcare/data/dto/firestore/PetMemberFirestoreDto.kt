package com.example.petcare.data.dto.firestore

import com.google.firebase.Timestamp

// @NOTE The data classes were ai generated basing on the database schema

data class PetMemberFirestoreDto (
    val id: String = "",
    val petId: String = "",
    val userId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_PET_ID = "petId"
        const val FIELD_USER_ID = "userId"
        const val FIELD_CREATED_AT = "createdAt"
    }
}
