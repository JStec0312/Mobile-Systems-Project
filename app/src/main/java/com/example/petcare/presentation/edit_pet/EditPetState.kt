package com.example.petcare.presentation.edit_pet

import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import kotlinx.datetime.LocalDate

data class EditPetState(
    val petId: String = "",
    val name: String = "",
    val species: speciesEnum = speciesEnum.dog,
    val breed: String = "",
    val sex: sexEnum = sexEnum.male,
    val birthDate: LocalDate? = null,
    val avatarThumbUrl: String? = null,

    val newAvatarThumbUrl: String? = null,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,

    val showSaveDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,

    val isLoading: Boolean = false,
    val error: String? = null,
)