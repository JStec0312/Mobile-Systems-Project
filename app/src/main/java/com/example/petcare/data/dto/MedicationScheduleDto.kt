package com.example.petcare.data.dto

data class MedicationScheduleDto(
    val medication_id: String,
    val start_date: com.google.firebase.Timestamp,
    val end_date: com.google.firebase.Timestamp? = null,
    val freq: com.example.petcare.common.frequencyEnum,
    val interval: Int? = null,
    val by_weekday: String? = null,
    val by_month_day: String? = null,
    val rrule_text: String? = null
)