package com.example.petcare.domain.model

import kotlinx.datetime.LocalDate

data class PetShareCode(
    val id: String,
    val petId: String,
    val code: String,
    val createdAt: LocalDate
)
