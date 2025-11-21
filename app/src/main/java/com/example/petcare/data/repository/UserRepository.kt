package com.example.petcare.data.repository

import com.example.petcare.data.dto.UserDto
import com.example.petcare.domain.repository.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.example.petcare.domain.model.User


class UserRepository  @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : IUserRepository {
    private fun users() = db.collection("users")

    override suspend fun createUser(user: User, password: String): UserDto {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): UserDto {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(userId: String) {
        TODO("Not yet implemented")
    }

}