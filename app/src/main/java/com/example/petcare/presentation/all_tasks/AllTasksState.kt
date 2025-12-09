package com.example.petcare.presentation.all_tasks

import com.example.petcare.domain.model.Task
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class AllTasksState(
    val selectedDate: LocalDate? = null,
    val allTasks: List<Task> = emptyList(),
    val tasksForSelectedDate: List<Task> = emptyList(),
    val dateText: String = "",
    val dayOfWeek: String = "",
    val allTasksCache: List<Task> = emptyList(),

    val isLoading: Boolean = false,
    val error: String? = null,
    val isRemoveSuccess: Boolean = false
)