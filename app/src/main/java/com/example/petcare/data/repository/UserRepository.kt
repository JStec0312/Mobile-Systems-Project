package com.example.petcare.data.repository

import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.User
import com.example.petcare.domain.repository.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : IUserRepository {
    private fun users() = db.collection("users")

    override suspend fun createUser(user: User, password: String): User {
        // Implementation will convert user to DTO, send to API, get DTO response, convert back to domain
        TODO("Not yet implemented")
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): User {
        // Implementation will get UserDto from API and convert to domain using .toDomain()
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(userId: String): User {
        // Implementation will get UserDto from API and convert to domain using .toDomain()
        TODO("Not yet implemented")
    }
}
