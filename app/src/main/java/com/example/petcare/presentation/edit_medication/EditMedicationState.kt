package com.example.petcare.presentation.edit_medication

import com.example.petcare.presentation.add_medication.MedRecurrenceType
import com.example.petcare.presentation.add_medication.MedicationForm
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class EditMedicationState(
    val medicationId: String = "",
    val name: String = "",
    val form: MedicationForm? = null,
    val dose: String = "",
    val notes: String = "",
    val startDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val endDate: LocalDate? = null,

    // Pola czasu i przypomnień (ujednolicone z AddMedication)
    val reminderTime: LocalTime? = null,
    val isReminderEnabled: Boolean = false,

    val isRecurring: Boolean = false,
    val recurrenceType: MedRecurrenceType = MedRecurrenceType.DAILY,
    val repeatInterval: Int = 1,
    val selectedDays: Set<DayOfWeek> = emptySet(),

    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val error: String? = null
)