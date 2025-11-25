package com.example.petcare.domain.repository

import com.example.petcare.domain.model.Medication
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure

interface IMedicationRepository {
    @Throws(Failure.ServerError::class, Failure.NetworkError::class, Failure.UnknownError::class)
    fun createMedication(medication: Medication)

    @Throws(Failure.ServerError::class, Failure.NetworkError::class, Failure.UnknownError::class, GeneralFailure.MedicationNotFound::class)
    fun deleteMedication(medicationId: String)

    @Throws(Failure.ServerError::class, Failure.NetworkError::class, Failure.UnknownError::class, GeneralFailure.MedicationNotFound::class, GeneralFailure.PetNotFound::class)
    fun listMedicationsForPet(petId: String): List<Medication>
}