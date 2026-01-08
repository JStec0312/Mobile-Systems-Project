package com.example.petcare.data.dto.firestore

import com.google.firebase.Timestamp

data class WalkTrackPointFirestoreDto(
    var id: String = "",
    var walkId: String? = null,
    var ts: Timestamp = Timestamp.now(),
    var lat: Double = 0.0,
    var lon: Double = 0.0,
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_WALK_ID = "walkId"
        const val FIELD_TS = "ts"
    }
}
