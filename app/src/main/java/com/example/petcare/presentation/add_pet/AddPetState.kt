package com.example.petcare.presentation.add_pet

import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.domain.model.Pet
import kotlinx.datetime.LocalDate

enum class AddPetMode {
    CREATE_NEW,
    ADD_BY_ID
}

data class AddPetState (
    val name: String = "",
    val species: speciesEnum = speciesEnum.dog,
    val breed: String = "",
    val sex: sexEnum = sexEnum.male,
    val birthDate: LocalDate? = null,
    val avatarThumbUrl: String? = null,

    val petIdToAdd: String = "",
    val foundPet: Pet? = null,

    val currentMode: AddPetMode = AddPetMode.CREATE_NEW,

    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccessful: Boolean = false
)