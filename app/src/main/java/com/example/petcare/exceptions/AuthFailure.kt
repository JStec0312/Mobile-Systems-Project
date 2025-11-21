package com.example.petcare.exceptions

sealed class AuthFailure(override val message: String?): Failure(message) {
     class EmailAlreadyInUse: AuthFailure(message = "Email already in use")
    class InvalidCredentials: AuthFailure("Invalid credentials")
    class UserNotFound: AuthFailure("User not found")
    class UserNotLoggedIn: AuthFailure("User not logged in")
    class PermissionDenied: AuthFailure("You do not have permission to perform this action")
}




