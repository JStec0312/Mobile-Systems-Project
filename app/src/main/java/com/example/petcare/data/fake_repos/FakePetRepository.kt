package com.example.petcare.data.fake_repos

import com.example.petcare.data.dto.PetDto
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.repository.IPetRepository

class FakePetRepository : IPetRepository {
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
}