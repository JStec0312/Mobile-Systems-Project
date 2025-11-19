package com.example.petcare.domain.model
import com.example.petcare.common.frequencyEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.data.dto.MedicationScheduleDto
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
) {
    fun toDto(): MedicationScheduleDto{
        return MedicationScheduleDto(
            medicationId = this.medicationId,
            startDate = DateConverter.localDateToString(this.startDate),
            endDate = DateConverter.localDateToString(this.endDate),
            freq = this.freq,
            interval = this.interval,
            byWeekday = this.byWeekday,
            byMonthDay = this.byMonthDay,
            rruleText = this.rruleText
        )
    }
}