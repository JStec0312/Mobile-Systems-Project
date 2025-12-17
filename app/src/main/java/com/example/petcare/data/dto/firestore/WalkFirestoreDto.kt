package com.example.petcare.data.dto.firestore

import com.google.firebase.Timestamp

data class WalkFirestoreDto(
    val id: String = "",
    val petId: String = "",
    val startedAt: Timestamp = Timestamp.now(),
    var endedAt: Timestamp? = null,
    var durationSec: Int? = null,
    var distanceMeters: Int? = null,
    var steps: Int? = null,
    var pending: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_PET_ID = "petId"
        const val FIELD_STARTED_AT = "startedAt"
        const val FIELD_CREATED_AT = "createdAt"
    }
}
