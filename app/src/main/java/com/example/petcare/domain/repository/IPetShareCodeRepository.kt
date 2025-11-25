package com.example.petcare.domain.repository

import com.example.petcare.domain.model.PetShareCode
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure

interface IPetShareCodeRepository {

    @Throws (Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun getPetShareCodeByValue(shareCode: String): PetShareCode?

    @Throws (Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun deletePetShareCodeById(shareCodeId: String)

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun createPetShareCode(petShareCode: PetShareCode): PetShareCode
}