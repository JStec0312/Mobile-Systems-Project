package com.example.petcare.data.dto

import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum

data class PetDto (
    val id: String,
    val ownerUserId: String,
    val name: String,
    val species: speciesEnum,
    val breed: String?,
    val sex : sexEnum = sexEnum.unknown,
    val birthDate: String? = null,
    val avatarThumbUrl : String? = null,
    val createdAt: String? = null
)
