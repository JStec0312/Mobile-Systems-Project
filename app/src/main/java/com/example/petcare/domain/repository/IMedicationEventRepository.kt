package com.example.petcare.domain.repository

import com.example.petcare.domain.model.Medication
import kotlinx.datetime.LocalDate

interface IMedicationEventRepository {
    fun createByMedication(medication: Medication)
}