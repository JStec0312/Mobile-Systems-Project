package com.example.petcare.domain.providers

import java.util.UUID

interface IUserProvider {
    fun getUserId(): UUID
    fun setUserId(id: UUID)
}