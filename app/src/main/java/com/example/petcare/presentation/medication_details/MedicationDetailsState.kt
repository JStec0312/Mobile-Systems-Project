package com.example.petcare.presentation.medication_details

import com.example.petcare.domain.model.Medication

data class MedicationDetailsState(
    val medication: Medication? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)