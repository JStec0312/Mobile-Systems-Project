package com.example.petcare.domain.providers.implementation
import com.example.petcare.domain.model.Pet
import  com.example.petcare.domain.providers.IPetProvider
import java.util.UUID

class PetProvider: IPetProvider {
    private var currentPetId: String? = null
    private var pet: Pet? = null;
    override fun getCurrentPetId(): String {
        return currentPetId ?: throw IllegalStateException("Current pet ID is not set")
    }

    override fun setCurrentPetId(id: String) {
        currentPetId = id
    }
    override fun setCurrentPet(pet: Pet) {
        this.pet= pet
    }
    override fun getCurrentPet(): Pet {
        return pet ?: throw IllegalStateException("Current pet is not set")
    }
}