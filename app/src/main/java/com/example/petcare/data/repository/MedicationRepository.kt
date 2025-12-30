package com.example.petcare.data.repository

import com.example.petcare.data.dto.firestore.MedicationFirestoreDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class MedicationRepository( db: FirebaseFirestore) : IMedicationRepository {
    private val col = db.collection(FirestorePaths.MEDICATIONS);
    override suspend fun createMedication(medication: Medication) {
        try {
            val firestoreDto = medication.toFirestoreDto();
            col.document(medication.id).set(firestoreDto).await()
        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "createMedication")
        }

    }

    override suspend fun deleteMedication(medicationId: String) {
        try{
            val docRef = col.document(medicationId)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()){
                throw GeneralFailure.MedicationNotFound("Medication with id $medicationId not found")
            }
            docRef.delete().await()
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "deleteMedication")
        }
    }

    override suspend fun listMedicationsForPet(petId: String): List<Medication> {
        try{
            val querySnapshot = col.whereEqualTo("petId", petId).get().await()
            if (querySnapshot.isEmpty){
                throw GeneralFailure.MedicationNotFound("No medications found for pet with id $petId")
            }
            val result =  querySnapshot.documents.map {it.toObject(MedicationFirestoreDto::class.java)}
            if (result == null){
                throw GeneralFailure.DataCorruption("Failed to parse medications for pet with id $petId")
            }
            return  result.filterNotNull().map { it.toDomain() }
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "listMedicationsForPet")
        }
    }

    override suspend fun getMedicationById(medicationId: String): Medication {
        try {
            val docRef = col.document(medicationId)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                throw GeneralFailure.MedicationNotFound("Medication with id $medicationId not found")
            }
            val firestoreDto = snapshot.toObject(MedicationFirestoreDto::class.java)
            if (firestoreDto == null) {
                throw GeneralFailure.MedicationNotFound("Medication with id $medicationId not found")
            }
            return firestoreDto.toDomain();
        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "getMedicationById")
        }
    }

    override suspend fun updateMedication(medication: Medication) {
        try {
            val firestoreDto = medication.toFirestoreDto();
            val docRef = col.document(medication.id)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                throw GeneralFailure.MedicationNotFound("Medication with id ${medication.id} not found")
            }
            docRef.set(firestoreDto).await()
        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "updateMedication")
        }
    }
}