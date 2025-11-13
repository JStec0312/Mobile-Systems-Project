package com.example.petcare.domain.use_case.sign_up

import com.example.petcare.common.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val shouldFail: Boolean = false,
    private val delayMs: Long = 600
) {

    operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String

    ): Flow<Resource<Unit>> = flow {
        // Prosta walidacja danych
        if (name.isBlank()) {
            emit(Resource.Error<Unit>("Name cannot be blank"))
            return@flow
        };
        if (email.isBlank()) {
            emit(Resource.Error<Unit>("Email cannot be blank"))
            return@flow
        };
        if (password.isBlank()) {
            emit(Resource.Error<Unit>("Password cannot be blank"))
            return@flow

        };
        emit(Resource.Loading<Unit>())
        delay(delayMs)
        if (shouldFail) {
            emit(Resource.Error<Unit>("Something went wrong"))
            return@flow
        }
        // Udana rejestracja (na razie bez repo)
        emit(Resource.Success(Unit))
        return@flow
    }
}