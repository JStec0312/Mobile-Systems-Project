package com.example.petcare.presentation.task_details

import com.example.petcare.domain.model.Task

data class TaskDetailsState (
    val task: Task? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)


