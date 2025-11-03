package com.example.petcare.domain.providers.implementation
import  com.example.petcare.domain.providers.IPetProvider
import java.util.UUID

class PetProvider: IPetProvider {
    private var currentPetId: UUID? = null

    override fun getCurrentPetId(): UUID {
        return currentPetId ?: throw IllegalStateException("Current pet ID is not set")
    }

    override fun setCurrentPetId(id: UUID) {
        currentPetId = id
    }
}