package com.example.petcare.domain.model
import com.google.firebase.Timestamp
import com.example.petcare.common.frequencyEnum
data class MedicationScheduleDto(
    val medication_id: String,        // subkolekcja pod medications/{medId}/schedules => pole opcjonalne
    val start_date: Timestamp,
    val end_date: Timestamp? = null,
    val freq: frequencyEnum,
    val interval: Int? = null,
    val by_weekday: String? = null,
    val by_month_day: String? = null,
    val rrule_text: String? = null
)