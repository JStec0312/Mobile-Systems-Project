package com.example.petcare.domain.model


import java.util.UUID

data class User (
    val id: String,
    val email: String,
    val displayName: String
)