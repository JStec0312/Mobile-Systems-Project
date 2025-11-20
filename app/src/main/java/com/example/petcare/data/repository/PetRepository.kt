package com.example.petcare.data.repository

import com.example.petcare.data.dto.PetDto
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.repository.IPetRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PetRepository(auth: FirebaseAuth, db: FirebaseFirestore) : IPetRepository {
    override suspend fun createPet(
        userId: String?,
        pet: Pet,
        avatarByteArray: ByteArray?
    ): PetDto {
        TODO("Not yet implemented")
    }

    override suspend fun getPetById(petId: String): PetDto {
        TODO("Not yet implemented")
    }

    override suspend fun getPets(userId: String): List<PetDto> {
        TODO("Not yet implemented")
    }
}