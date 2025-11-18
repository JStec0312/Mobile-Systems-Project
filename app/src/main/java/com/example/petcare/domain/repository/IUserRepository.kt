package com.example.petcare.domain.repository

import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import com.example.petcare.data.dto.UserDto

interface IUserRepository {

    /**
     * Register new user
     * @throws AuthFailure.EmailAlreadyInUse
     * @throws Failure.NetworkError
     * @throws Failure.ServerError
     * @throws Failure.UnknownError
     */
    @Throws(AuthFailure.EmailAlreadyInUse::class, Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun createUser(email: String, password: String,  displayName: String): UserDto

    /**
     * Sign in with email and password
     * @throws AuthFailure.InvalidCredentials
     * @throws AuthFailure.UserNotFound
     * @throws Failure.NetworkError
     * @throws Failure.ServerError
     * @throws Failure.UnknownError
     */
    @Throws(AuthFailure.InvalidCredentials::class, AuthFailure.UserNotFound::class, Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun signInWithEmailAndPassword(email: String, password: String): UserDto

    @Throws (AuthFailure.UserNotFound::class, Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class )
    suspend fun getUserById(userId: String)
}