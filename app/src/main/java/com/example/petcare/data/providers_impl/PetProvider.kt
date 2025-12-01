package com.example.petcare.domain.providers.implementation
import com.example.petcare.domain.model.Pet
import  com.example.petcare.domain.providers.IPetProvider
import java.util.UUID
import javax.inject.Inject

class PetProvider @Inject constructor() : IPetProvider {
    private var currentPetId: String? = null
    private var pet: Pet? = null;
    override fun getCurrentPetId(): String {
        return currentPetId ?: throw IllegalStateException("Current pet ID is not set")
    }

    override fun setCurrentPetId(id: String?) {
        currentPetId = id
    }
    override fun setCurrentPet(pet: Pet) {
        this.pet= pet
        this.currentPetId = pet.id
    }
    override fun getCurrentPet(): Pet {
        return pet ?: throw IllegalStateException("Current pet is not set")
    }
}