package com.example.petcare.domain.providers.implementation

import android.R
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.exceptions.AuthFailure
import java.util.UUID

class UserProvider: IUserProvider {

    private lateinit  var userId: String;
    override fun getUserId(): String {
        if (!this::userId.isInitialized){
            throw AuthFailure.UserNotLoggedIn()
        } else {
            return userId
        }
    }
    override fun setUserId(id: String) {
        this.userId = id
    }



}