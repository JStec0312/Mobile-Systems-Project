package com.example.petcare.exceptions

sealed class AuthFailure(override val message: String): Failure(message) {
    object EmailAlreadyInUse: AuthFailure("Email already in use")
    object InvalidCredentials: AuthFailure("Invalid credentials")
    object UserNotFound: AuthFailure("User not found")


}