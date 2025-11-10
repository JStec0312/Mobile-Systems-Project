package com.example.petcare.domain.use_case.sign_up

import javax.inject.Inject

class SignUpUseCase @Inject constructor() {

    operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        // Prosta walidacja danych
        if (name.isBlank()) return Result.failure(Exception("Name cannot be empty"))
        if (email.isBlank()) return Result.failure(Exception("Email cannot be empty"))
        if (password.length < 6) return Result.failure(Exception("Password too short"))
        if (password != confirmPassword) return Result.failure(Exception("Passwords do not match"))

        // Udana rejestracja (na razie bez repo)
        return Result.success(Unit)
    }
}