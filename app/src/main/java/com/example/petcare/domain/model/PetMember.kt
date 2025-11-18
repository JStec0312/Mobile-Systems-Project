package com.example.petcare.domain.model

import kotlinx.datetime.LocalDate

data class PetMember(
    val id: String,
    val petId: String,
    val userId: String,
    val createdAt: LocalDate
)