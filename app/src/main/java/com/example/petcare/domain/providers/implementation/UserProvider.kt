package com.example.petcare.domain.providers.implementation

import com.example.petcare.domain.providers.IUserProvider
import java.util.UUID

class UserProvider: IUserProvider {

    private var userId: UUID = UUID.randomUUID()
    override fun getUserId(): UUID {
        return userId
    }
    override fun setUserId(id: UUID) {
        this.userId = id;
    }



}