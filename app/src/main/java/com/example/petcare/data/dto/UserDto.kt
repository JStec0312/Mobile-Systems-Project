package com.example.petcare.data.dto

import com.example.petcare.domain.model.User

data class UserDto(
    val email: String,
    val displayName: String,
    val id: String,
){
    fun toModel() : User {
        return User(
            email = this.email,
            displayName = this.displayName,
            id = this.id
        )
    }
}
