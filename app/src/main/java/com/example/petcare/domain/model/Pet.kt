package com.example.petcare.domain.model
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.google.firebase.Timestamp

data class Pet(
    val id: String,
    val ownerUserId: String,
    val name: String,
    val species: speciesEnum,
    val breed: String?,
    val sex: sexEnum = sexEnum.unknown,
    val birthDate: Timestamp?,
    val avatarThumbUrl: String?,
    val createdAt: Timestamp?
)
