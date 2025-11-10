package com.example.petcare.data.dto

import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.google.firebase.Timestamp

data class PetDto (
    val ownerUserId: String,
    val name: String,
    val species: speciesEnum,
    val breed: String?,
    val sex : sexEnum = sexEnum.unknown,
    val birthDate: Timestamp? = null,
    val avatarThumbUrl : String? = null,
    val created_at: Timestamp? = null
)