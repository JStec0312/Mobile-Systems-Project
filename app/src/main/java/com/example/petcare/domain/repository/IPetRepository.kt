package com.example.petcare.domain.repository

import com.example.petcare.domain.model.Pet
import com.example.petcare.exceptions.Failure
import kotlin.jvm.Throws

interface IPetRepository {
    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun createPet(userId: String?, pet: Pet, avatarByteArray: ByteArray?): Pet
}


