package com.example.petcare.data.fake_repos

import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.repository.IPetRepository

class FakePetRepository : IPetRepository {
    override suspend fun createPet(
        userId: String?,
        pet: Pet,
        avatarByteArray: ByteArray?
    ): Pet {
        TODO("Not yet implemented")
    }
}