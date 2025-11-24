package com.example.petcare.domain.providers

import com.example.petcare.domain.model.Pet
import java.util.UUID

interface IPetProvider {
     fun getCurrentPetId(): String?;
        fun setCurrentPetId(id: String? = null);
    fun setCurrentPet(pet: Pet);
    fun getCurrentPet(): Pet;
}
