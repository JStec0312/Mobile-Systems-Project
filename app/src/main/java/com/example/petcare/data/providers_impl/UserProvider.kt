package com.example.petcare.domain.providers.implementation

import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.exceptions.AuthFailure
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import javax.inject.Inject

class UserProvider @Inject constructor(
    private val auth: FirebaseAuth
) : IUserProvider {

    override fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    override fun setUserId(id: String) {
        val firebaseUid = auth.currentUser?.uid

        if (firebaseUid == null) {
            Timber.w("setUserId($id) called but FirebaseAuth.currentUser is null")
            throw AuthFailure.UserNotLoggedIn()
        }

        if (firebaseUid != id) {
            Timber.w("setUserId($id) != FirebaseAuth uid=$firebaseUid. This is a bug in auth flow.")
            throw AuthFailure.UserNotLoggedIn()
        }

    }

    override fun clearUserData() {
        try {
            auth.signOut()
        } catch (t: Throwable) {
            Timber.w(t, "Error while signing out from FirebaseAuth")
        }
    }
}
