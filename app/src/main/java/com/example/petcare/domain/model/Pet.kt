package com.example.petcare.domain.model
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import kotlinx.datetime.Instant
import java.util.UUID

data class Pet(
    val id: String,
    val ownerUserId: String,
    val name: String,
    val species: speciesEnum,
    val breed: String,
    val sex: sexEnum = sexEnum.unknown,
    val birthDate: Instant,
    val avatarThumbUrl: String?,
    val createdAt: Instant
)
