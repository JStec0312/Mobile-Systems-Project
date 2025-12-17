package com.example.petcare.data.dto.fake

data class PetShareCodeDto(
    val id: String,
    val petId: String,
    val code: String,                 // np. "A1B2C3D4"
    val expiresAt: String,
    val createdAt: String? = null,
)
