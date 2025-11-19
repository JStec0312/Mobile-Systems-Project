package com.example.petcare.presentation.my_pets

import com.example.petcare.domain.model.Pet

data class MyPetsState(
    val pets: List<Pet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)