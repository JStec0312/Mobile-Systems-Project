package com.example.petcare.data.repository

import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.repository.IPetRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PetRepository(auth: FirebaseAuth, db: FirebaseFirestore) : IPetRepository {

    override suspend fun createPet(
        pet: Pet,
        avatarByteArray: ByteArray?
    ): Pet {
        // Implementation will convert pet to DTO, send to API, get DTO response, convert back to domain
        TODO("Not yet implemented")
    }

    override suspend fun getPetById(petId: String): Pet {
        // Implementation will get DTO from API and convert to domain using .toDomain()
        TODO("Not yet implemented")
    }

    override suspend fun deletePetById(petId: String, userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getPetsByIds(petIds: List<String>): List<Pet> {
        // Implementation will get List<PetDto> from API and convert using .map { it.toDomain() }
        TODO("Not yet implemented")
    }

    override suspend fun editPet(
        pet: Pet,
        avatarByteArray: ByteArray?
    ): Pet {
        // Implementation will convert pet to DTO, send to API, get DTO response, convert back to domain
        TODO("Not yet implemented")
    }

}