package com.example.petcare.domain.model

import java.util.UUID

data class Breed (
    val id: UUID,
    val species: speciesEnum,
    val name: String
)