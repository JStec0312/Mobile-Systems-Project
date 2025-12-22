package com.example.petcare.data.repository

import com.example.petcare.data.dto.firestore.UserFirestoreDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.domain.model.User
import com.example.petcare.domain.repository.IUserRepository
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : IUserRepository {

    private fun users() = db.collection(FirestorePaths.USERS)

    override suspend fun createUser(user: User, password: String): User {
        try {
            val res = auth.createUserWithEmailAndPassword(user.email, password).await()
            val fbUser = res.user ?: throw Failure.UnknownError()
            if (user.displayName.isNotBlank()) {
                val req = UserProfileChangeRequest.Builder()
                    .setDisplayName(user.displayName)
                    .build()
                fbUser.updateProfile(req).await()
            }

            val dto = user.toFirestoreDto()
            users().document(fbUser.uid).set(dto).await()

            return user.copy(id = fbUser.uid)

        } catch (e: FirebaseAuthUserCollisionException) {
            throw AuthFailure.EmailAlreadyInUse()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // zly email format itp.
            throw AuthFailure.InvalidCredentials()
        } catch (e: FirebaseNetworkException) {
            throw Failure.NetworkError()
        } catch (t: Throwable) {
            Timber.d("SignUpUseCase invoke: Failure during sign up: ${t}")
            throw Failure.ServerError()
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): User {
        try {
            val res = auth.signInWithEmailAndPassword(email, password).await()
            val fbUser = res.user ?: throw AuthFailure.UserNotFound()

            val doc = users().document(fbUser.uid).get().await()
            val dto = doc.toObject(UserFirestoreDto::class.java)

            // fallback: jak nie ma doca, bierzemy dane z Auth (zeby nie wysypywac appki)
            val displayName = dto?.displayName ?: (fbUser.displayName ?: "")
            val outEmail = dto?.email ?: (fbUser.email ?: email)

            return dto!!.toDomain();

        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthFailure.UserNotFound()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthFailure.InvalidCredentials()
        } catch (e: FirebaseNetworkException) {
            throw Failure.NetworkError()
        } catch (t: Throwable) {
            throw Failure.ServerError()
        }
    }

    override suspend fun getUserById(userId: String): User {
        try {
            val doc = users().document(userId).get().await()
            val dto = doc.toObject(UserFirestoreDto::class.java) ?: throw AuthFailure.UserNotFound()

            return User(
                id = userId,
                email = dto.email ?: throw AuthFailure.UserNotFound(),
                displayName = dto.displayName ?: ""
            )

        } catch (e: FirebaseNetworkException) {
            throw Failure.NetworkError()
        } catch (t: Throwable) {
            throw Failure.ServerError()
        }
    }
}
