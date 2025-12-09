package com.example.petcare.data.fake_repos

import com.example.petcare.data.dto.MedicationDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure

class FakeMedicationRepository : IMedicationRepository {
    private val medications = mutableListOf<MedicationDto>()

    override fun createMedication(medication: Medication) {
        if (medication.name == "Server error"){
            throw Failure.ServerError("Simulated server error")
        } else if (medication.name == "Network error"){
            throw Failure.NetworkError("Simulated network error")
        } else if (medication.name == "Unknown error"){
            throw Failure.UnknownError("Simulated unknown error")
        }
        medications.add(medication.toDto())
    }

    override fun deleteMedication(medicationId: String) {
        val medication = medications.find { it.id == medicationId }
        if (medication == null){
            throw GeneralFailure.MedicationNotFound("Medication with id $medicationId not found")
        }
        medications.remove(medication)
    }

    override fun listMedicationsForPet(petId: String): List<Medication> {
        val medsForPet = medications.filter { it.petId == petId }
        if (medsForPet.isEmpty()){
            throw GeneralFailure.MedicationNotFound("No medications found for pet")
        }
        if (petId == "Server error"){
            throw Failure.ServerError("Simulated server error")
        } else if (petId == "Network error"){
            throw Failure.NetworkError("Simulated network error")
        } else if (petId == "Unknown error"){
            throw Failure.UnknownError("Simulated unknown error")
        } else if (petId == "Pet not found"){
            throw GeneralFailure.PetNotFound("Pet with id $petId not found")
        }
        return medsForPet.map { it.toDomain() }
    }

    override fun getMedicationById(medicationId: String): Medication {
        val medication = medications.find { it.id == medicationId }
        if (medication == null) {
            throw GeneralFailure.MedicationNotFound("Medication with id $medicationId not found")
        }
        if (medicationId == "Server error") {
            throw Failure.ServerError("Simulated server error")
        } else if (medicationId == "Network error") {
            throw Failure.NetworkError("Simulated network error")
        } else if (medicationId == "Unknown error") {
            throw Failure.UnknownError("Simulated unknown error")
        }
        return medication.toDomain()
    }
}