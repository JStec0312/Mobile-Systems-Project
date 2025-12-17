package com.example.petcare.data.fake_providers

import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.providers.IPetProvider

class FakePetProvider : IPetProvider {
    private var currentPetId: String? = null
    private var currentPet: Pet? = null

    override fun getCurrentPetId(): String? {
        return currentPetId
    }

    override fun setCurrentPetId(id: String?) {
        currentPetId = id
    }

    override fun setCurrentPet(pet: Pet) {
        currentPet = pet
        currentPetId = pet.id
    }

    override fun getCurrentPet(): Pet {
        return currentPet ?: throw IllegalStateException("No pet set")
    }
}

