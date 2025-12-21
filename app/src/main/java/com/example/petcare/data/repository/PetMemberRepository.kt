package com.example.petcare.data.repository

import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.domain.model.PetMember
import com.example.petcare.domain.repository.IPetMemberRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PetMemberRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
): IPetMemberRepository {
    private val col = db.collection(FirestorePaths.PET_MEMBERS)
    override suspend fun addPetMember(petMemember: PetMember) {
        try{
            val petMemberFirestoreDto = petMemember.toFirestoreDto()
            col.document(petMemember.id).set(petMemberFirestoreDto).await();
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "addPetMember");
        }
    }

    override suspend fun getPetIdsByUserId(userId: String): List<String> {
        try{
            val querySnapshot = col.whereEqualTo("userId", userId).get().await()
            val petIds = querySnapshot.documents.mapNotNull { it.getString("petId") }
            return petIds
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "getPetIdsByUserId");
        }
    }

    override suspend fun isUserPetMember(userId: String, petId: String): Boolean {
        try{
            val querySnapshot = col
                .whereEqualTo("userId", userId)
                .whereEqualTo("petId", petId)
                .get()
                .await()
            return !querySnapshot.isEmpty
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "isUserPetMember");
        }
    }
}