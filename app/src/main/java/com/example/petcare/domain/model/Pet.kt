package com.example.petcare.domain.model
import java.util.UUID
import java.time.Instant
import java.time.LocalDate

data class Pet(
    val id: UUID,
    val owner_user_id: UUID,
    val name: String,
    val species: speciesEnum,
    val breed_id: UUID?,
    val sex: sexEnum = sexEnum.unknown,
    val birth_date: LocalDate?,
    val avatar_thumb_url: String?,
    val created_at: Instant
)
