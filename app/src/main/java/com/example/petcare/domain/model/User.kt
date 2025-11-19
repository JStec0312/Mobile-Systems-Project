package com.example.petcare.domain.model


import com.example.petcare.data.dto.UserDto
import java.util.UUID

data class User (
    val id: String,
    val email: String,
    val displayName: String
) {
    fun toDto(): UserDto {
        return UserDto(
            id = this.id,
            email = this.email,
            displayName = this.displayName
        )
    }
}