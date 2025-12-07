package com.example.petcare.presentation.add_task

import com.example.petcare.common.taskTypeEnum
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AddTaskState(
    val title: String = "",
    val type: taskTypeEnum? = null,
    val notes: String = "",
    val selectedDate: LocalDate? = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val selectedTime: LocalTime? = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time,
    val isRecurring: Boolean = false,
    val recurrenceType: RecurrenceType = RecurrenceType.DAILY,
    val repeatInterval: Int = 1,
    val selectedDaysOfWeek: Set<DayOfWeek> = emptySet(),
    val recurrenceEndDate: LocalDate? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccessful: Boolean = false
    )

enum class RecurrenceType {
    DAILY,
    WEEKLY,
    MONTHLY
}