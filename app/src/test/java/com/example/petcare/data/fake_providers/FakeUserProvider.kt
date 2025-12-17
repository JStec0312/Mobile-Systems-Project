package com.example.petcare.data.fake_providers

import com.example.petcare.domain.providers.IUserProvider

class FakeUserProvider : IUserProvider {
    private var userId: String? = null

    override fun getUserId(): String? {
        return userId
    }

    override fun setUserId(id: String) {
        userId = id
    }

    override fun clearUserData() {
        userId = null
    }
}

