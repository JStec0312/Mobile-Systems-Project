package com.example.petcare.presentation.sign_in

data class SignInState(
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false
)