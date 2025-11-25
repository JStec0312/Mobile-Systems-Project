package com.example.petcare.domain.model
import com.example.petcare.common.frequencyEnum
import kotlinx.datetime.LocalDate

data class MedicationSchedule(
    val medicationId: String,        // subkolekcja pod medications/{medId}/schedules => pole opcjonalne
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val freq: frequencyEnum,
    val interval: Int? = null,
    val byWeekday: String? = null,
    val byMonthDay: String? = null,
    val rruleText: String? = null
)
