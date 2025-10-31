package com.example.petcare.domain.model


import java.util.UUID

data class User (
    val id: UUID,
    val email: String,
    val display_name: String
)