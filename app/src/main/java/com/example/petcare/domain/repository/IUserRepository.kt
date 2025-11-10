package com.example.petcare.domain.repository

import com.example.petcare.data.dto.UserDto

interface IUserRepository {
    suspend fun createUser(email: String, password: String,  displayName: String): UserDto
}