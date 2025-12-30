package com.example.petcare.presentation.add_medication

import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AddMedicationState(
    val name: String = "",
    val form: MedicationForm? = null,
    val dose: String = "",
    val notes: String = "",

    val startDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val endDate: LocalDate? = null,
    val reminderTime: LocalTime? = null,
    val isReminderEnabled: Boolean = true,

    val isRecurring: Boolean = false,
    val recurrenceType: MedRecurrenceType = MedRecurrenceType.DAILY,
    val repeatInterval: Int = 1,
    val selectedDays: Set<DayOfWeek> = emptySet(),

    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccessful: Boolean = false
)

enum class MedicationForm {
    TABLET, CAPSULE, SYRUP, INJECTION, DROPS, OINTMENT, OTHER
}

enum class MedRecurrenceType {
    DAILY, WEEKLY, MONTHLY, AS_NEEDED
}