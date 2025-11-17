package com.example.petcare.domain.repository

import com.example.petcare.data.dto.UserDto

interface IUserRepository {

    /**
     * Register new user
     * @throws AuthFailure.EmailAlreadyInUse
     * @throws Failure.NetworkError
     * @throws Failure.ServerError
     * @throws Failure.UnknownError
     */
    suspend fun createUser(email: String, password: String,  displayName: String): UserDto

    /**
     * Sign in with email and password
     * @throws AuthFailure.InvalidCredentials
     * @throws AuthFailure.UserNotFound
     * @throws Failure.NetworkError
     * @throws Failure.ServerError
     * @throws Failure.UnknownError
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): UserDto

}