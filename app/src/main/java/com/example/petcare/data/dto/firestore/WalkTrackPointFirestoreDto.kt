package com.example.petcare.data.dto.firestore

import com.google.firebase.Timestamp

data class WalkTrackPointFirestoreDto(
    val id: String = "",
    val walkId: String? = null,
    val ts: Timestamp = Timestamp.now(),
    val lat: Double = 0.0,
    val lon: Double = 0.0,
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_WALK_ID = "walkId"
        const val FIELD_TS = "ts"
    }
}
