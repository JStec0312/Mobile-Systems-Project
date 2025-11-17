package com.example.petcare.domain.model
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import java.util.UUID

data class Pet(
    val id: UUID,
    val ownerUserId: UUID,
    val name: String,
    val species: speciesEnum,
    val breed: String,
    val sex: sexEnum = sexEnum.unknown,
    val birthDate: kotlinx.datetime.Instant,
    val avatarThumbUrl: String?,
    val createdAt: kotlinx.datetime.Instant
)
