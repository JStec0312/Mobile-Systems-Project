package com.example.petcare.data.dto
import com.google.firebase.Timestamp


data class PetShareCodeDto(
    val pet_id: String,
    val code: String,                 // np. "A1B2C3D4"
    val expires_at: Timestamp? = null,
    val created_at: Timestamp? = null
)
