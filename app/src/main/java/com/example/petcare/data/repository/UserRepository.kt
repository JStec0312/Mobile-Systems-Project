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


class UserRepository  @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : IUserRepository {
    private fun users() = db.collection("users")
    override suspend fun createUser(email: String, password: String, displayName : String): UserDto{
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val u = auth.currentUser
            if (u == null) {
                throw Exception("User not found")
            }
            if (displayName.isNotEmpty()) {
                val req = userProfileChangeRequest { this.displayName = displayName }
                u.updateProfile(req).await()
            }
            val id = u.uid

            val docref = users().document(u.uid)
            val profile = mapOf(
                "display_name" to u.displayName,
                "email" to u.email,
                "created_at" to FieldValue.serverTimestamp()
            )
            docref.set(profile).await();
            Log.d("UserRepository add user", "User created successfully")
            return UserDto(u.email!!, u.displayName!!, id = u.uid)


        } catch (e: Exception) {
            error("Error creating user: ${e.message}")
        }
    }

}