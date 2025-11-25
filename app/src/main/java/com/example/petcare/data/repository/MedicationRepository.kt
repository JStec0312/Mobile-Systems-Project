package com.example.petcare.data.repository

import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.repository.IMedicationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MedicationRepository(auth: FirebaseAuth, db: FirebaseFirestore) : IMedicationRepository {
    override fun createMedication(medication: Medication) {
        TODO("Not yet implemented")
    }

    override fun deleteMedication(medicationId: String) {
        TODO("Not yet implemented")
    }

    override fun listMedicationsForPet(petId: String): List<Medication> {
        TODO("Not yet implemented")
    }
}