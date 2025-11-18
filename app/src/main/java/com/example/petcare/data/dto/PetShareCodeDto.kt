package com.example.petcare.data.dto
import com.google.firebase.Timestamp


data class PetShareCodeDto(
    val petId: String,
    val code: String,                 // np. "A1B2C3D4"
    val expiresAt: String? = null,
    val createdAt: String? = null
)
