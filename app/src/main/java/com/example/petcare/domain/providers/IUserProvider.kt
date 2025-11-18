package com.example.petcare.domain.providers

import java.util.UUID

interface IUserProvider {
    fun getUserId(): String?
    fun setUserId(id: String)
}