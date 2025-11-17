package com.example.petcare.data.dto

import com.example.petcare.domain.model.User
import java.util.UUID

data class UserDto(
    val email: String,
    val displayName: String,
    val id: UUID,
){
    fun toModel() : User {
        return User(
            email = this.email,
            displayName = this.displayName,
            id = this.id
        )
    }
}
