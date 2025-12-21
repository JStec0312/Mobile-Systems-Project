package com.example.petcare.data.repository

import com.example.petcare.data.dto.firestore.PetShareCodeFirestoreDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.domain.model.PetShareCode
import com.example.petcare.domain.repository.IPetShareCodeRepository
import com.example.petcare.exceptions.GeneralFailure
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PetShareCodeRepository(db: FirebaseFirestore) : IPetShareCodeRepository {
    private val col = db.collection(FirestorePaths.PET_SHARE_CODES)
    override suspend fun getPetShareCodeByValue(shareCode: String): PetShareCode? {
        try{
            val querySnapshot = col
                .whereEqualTo("code", shareCode)
                .get()
                .await()
            val document = querySnapshot.documents.firstOrNull()
            if (document == null){
                return null
            }
            val dto = document.toObject(PetShareCodeFirestoreDto::class.java)
            if (dto == null){
                throw GeneralFailure.DataCorruption("Failed to parse PetShareCodeFirestoreDto");
            }
            return dto.toDomain()
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "getPetShareCodeByValue");
        }

    }

    override suspend fun deletePetShareCodeById(shareCodeId: String) {
        try{
            col.document(shareCodeId).delete().await();
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "deletePetShareCodeById");
        }
    }

    override suspend fun createPetShareCode(petShareCode: PetShareCode): PetShareCode {
        try{
            val dto = petShareCode.toFirestoreDto();
            col.document(petShareCode.id).set(dto).await();
            return petShareCode
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "createPetShareCode");
        }
    }
}