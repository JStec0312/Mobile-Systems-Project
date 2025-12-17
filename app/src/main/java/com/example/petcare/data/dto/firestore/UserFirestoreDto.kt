package com.example.petcare.data.dto.firestore

data class UserFirestoreDto(
    val email: String = "",
    val displayName: String = "",
    val id: String = "",
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_EMAIL = "email"
    }
}
