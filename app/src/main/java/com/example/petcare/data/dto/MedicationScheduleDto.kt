package com.example.petcare.data.dto

data class MedicationScheduleDto(
    val medicationId: String,
    val startdate: String,
    val endDate: String? = null,
    val freq: com.example.petcare.common.frequencyEnum,
    val interval: Int? = null,
    val byWeekday: String? = null,
    val byMonthDay: String? = null,
    val rruleText: String? = null
)