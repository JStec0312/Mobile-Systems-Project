package com.example.petcare.data.repository

import com.example.petcare.data.dto.firestore.PetFirestoreDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toDto
import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.exceptions.GeneralFailure
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.tasks.await

class PetRepository( private  val db: FirebaseFirestore, private  val storage: FirebaseStorage) : IPetRepository {
    private final var col = db.collection(FirestorePaths.PETS)

    override suspend fun createPet(
        pet: Pet,
        avatarByteArray: ByteArray?
    ): Pet {
        try{
            val avatarPath: String? = if (avatarByteArray != null) {
                val path = "pets/${pet.id}/avatar.jpg"
                val ref = storage.reference.child(path)

                val metadata = storageMetadata { contentType = "image/jpeg" }
                ref.putBytes(avatarByteArray, metadata).await()

                ref.downloadUrl.await().toString()
            } else null


            val petDto = pet.toFirestoreDto();
            petDto.avatarThumbUrl = avatarPath;
            col.document(pet.id).set(petDto).await();
            return petDto.toDomain();
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "createPet");
        }
    }

    override suspend fun getPetById(petId: String): Pet {
        try{
            val firebasePet = col.document(petId).get().await();
            if (!firebasePet.exists()){
                throw GeneralFailure.PetNotFound()
            }
            val petDto = firebasePet.toObject(PetFirestoreDto::class.java)
            if (petDto == null){
                throw GeneralFailure.DataCorruption("Pet data is null");
            }
            return petDto.toDomain()
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "getPetById");
        }
    }

    override suspend fun deletePetById(petId: String, userId: String) {
        try{
            val petDocRef = col.document(petId)
            val petSnapshot = petDocRef.get().await()
            if (!petSnapshot.exists()){
                throw GeneralFailure.PetNotFound()
            }
            petDocRef.delete().await()
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "deletePetById");
        }
    }

    override suspend fun getPetsByIds(petIds: List<String>): List<Pet> {
        try{
            if (petIds.isEmpty()){
                return emptyList()
            }
            val querySnapshot = col.whereIn("id", petIds).get().await()
            val pets = querySnapshot.documents.mapNotNull { doc ->
                val petDto = doc.toObject(PetFirestoreDto::class.java)
                petDto?.toDomain()
            }
            return pets
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "getPetsByIds");
        }
    }

    override suspend fun editPet(
        pet: Pet,
        avatarByteArray: ByteArray?
    ): Pet {
        // Implementation will convert pet to DTO, send to API, get DTO response, convert back to domain
        try{
            val petDto = pet.toFirestoreDto();
            if (!col.document(pet.id).get().await().exists()){
                throw GeneralFailure.PetNotFound()
            }
            col.document(pet.id).set(petDto).await();
            return petDto.toDomain();
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "editPet");
        }
    }

}