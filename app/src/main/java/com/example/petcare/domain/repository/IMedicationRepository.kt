package com.example.petcare.domain.repository

import com.example.petcare.domain.model.Medication
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure

interface IMedicationRepository {
    @Throws(Failure.ServerError::class, Failure.NetworkError::class, Failure.UnknownError::class)
    suspend fun createMedication(medication: Medication)

    @Throws(Failure.ServerError::class, Failure.NetworkError::class, Failure.UnknownError::class, GeneralFailure.MedicationNotFound::class)
    suspend fun deleteMedication(medicationId: String)

    @Throws(Failure.ServerError::class, Failure.NetworkError::class, Failure.UnknownError::class, GeneralFailure.MedicationNotFound::class, GeneralFailure.PetNotFound::class)
    suspend fun listMedicationsForPet(petId: String): List<Medication>

    @Throws
    suspend fun getMedicationById(medicationId: String): Medication

    suspend fun updateMedication(medication: Medication)

}