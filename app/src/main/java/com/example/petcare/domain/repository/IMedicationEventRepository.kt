package com.example.petcare.domain.repository

import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.model.MedicationEvent
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

interface IMedicationEventRepository {
    suspend fun createByMedication(medication: Medication)
    suspend fun getUpcomingMedicationEventsForUserInDateRange(
        petIds: List<String>,
        startDate: Instant,
        endDate: Instant,
    ) : List<MedicationEvent>
}