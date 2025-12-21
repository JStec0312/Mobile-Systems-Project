package com.example.petcare.data.dto.firestore

data class TaskDeletedOccurrenceFirestoreDto(
    var seriesId: String? = null,
    var occurrenceAtMillis: Long? = null
) {
    companion object {
        const val FIELD_SERIES_ID = "seriesId"
        const val FIELD_OCCURRENCE_AT_MILLIS = "occurrenceAtMillis"
    }
}
