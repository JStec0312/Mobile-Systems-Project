package com.example.petcare.domain.use_case.sign_in

import javax.inject.Inject

class SignInUseCase @Inject constructor() {
    operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        // Prosta walidacja danych
        if (email.isBlank()) return Result.failure(Exception("Email cannot be empty"))
        if (password.length < 6) return Result.failure(Exception("Password too short"))
        // Udana rejestracja (na razie bez repo)
        return Result.success(Unit)
    }
}