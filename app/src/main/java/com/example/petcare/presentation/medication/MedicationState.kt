package com.example.petcare.presentation.medication

import com.example.petcare.domain.model.Medication
import kotlinx.datetime.LocalTime

data class MedicationState(
    val medications: List<Medication> = emptyList(),
    // To jest nasza lista na front, generowana w ViewModelu
    val upcomingDoses: List<UpcomingDoseUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// Prosty model tylko do wyświetlania na ekranie
data class UpcomingDoseUiModel(
    val medicationName: String,
    val time: LocalTime,
    val dayLabel: String // np. "Today", "Tomorrow"
)