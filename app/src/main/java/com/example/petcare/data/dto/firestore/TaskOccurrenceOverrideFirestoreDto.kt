package com.example.petcare.data.dto.firestore

data class TaskOccurrenceOverrideFirestoreDto(
    var seriesId: String? = null,
    var occurrenceAtMillis: Long? = null,
    var status: String? = null
) {
    companion object {
        const val FIELD_SERIES_ID = "seriesId"
        const val FIELD_OCCURRENCE_AT_MILLIS = "occurrenceAtMillis"
        const val FIELD_STATUS = "status"
    }
}
