package com.example.petcare.presentation.dashboard

import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.model.Task

data class PetDashboardState(
    val pet: Pet? = null,
    val tasks: List<Task> = emptyList(),

    val isLoading: Boolean = false,
    val error: String? = null,
)

