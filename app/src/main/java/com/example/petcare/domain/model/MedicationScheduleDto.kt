package com.example.petcare.domain.model
import com.google.firebase.Timestamp
import com.example.petcare.common.frequencyEnum
data class MedicationScheduleDto(
    val medicationId: String,        // subkolekcja pod medications/{medId}/schedules => pole opcjonalne
    val startDate: Timestamp,
    val endDate: Timestamp? = null,
    val freq: frequencyEnum,
    val interval: Int? = null,
    val byWeekday: String? = null,
    val byMonthDay: String? = null,
    val rruleText: String? = null
)