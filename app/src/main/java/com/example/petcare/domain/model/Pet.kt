package com.example.petcare.domain.model
import java.util.UUID

data class Pet(
    val id: UUID,
    val ownerUserId: UUID,
    val name: String,
    val species: speciesEnum,
    val breedId: UUID?,
    val sex: sexEnum = sexEnum.unknown,
    val birthDate: kotlinx.datetime.Instant,
    val avatarThumb_url: String?,
    val createdAt: kotlinx.datetime.Instant
)
