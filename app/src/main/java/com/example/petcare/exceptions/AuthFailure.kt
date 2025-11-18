package com.example.petcare.exceptions

sealed class AuthFailure(override val message: String): Failure(message) {
     class EmailAlreadyInUse: AuthFailure(message = "Email already in use")
    class InvalidCredentials: AuthFailure("Invalid credentials")
    class UserNotFound: AuthFailure("User not found")
}




