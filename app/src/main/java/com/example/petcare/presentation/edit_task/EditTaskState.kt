package com.example.petcare.presentation.edit_task

import com.example.petcare.common.taskTypeEnum
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class EditTaskState(
    val taskId: String = "",
    val seriesId: String? = null,
    val title: String = "",
    val notes: String = "",
    val type: taskTypeEnum? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,

    val showConfirmDialog: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
    val isSuccessful: Boolean = false
)