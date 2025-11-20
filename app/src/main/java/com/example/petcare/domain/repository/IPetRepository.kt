package com.example.petcare.domain.repository

import com.example.petcare.data.dto.PetDto
import com.example.petcare.domain.model.Pet
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlin.jvm.Throws

interface IPetRepository {
    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun createPet( pet: Pet, avatarByteArray: ByteArray?): PetDto

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class,
        AuthFailure.PermissionDenied::class, GeneralFailure.PetNotFound::class)
    suspend fun getPetById(petId: String): PetDto


    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun  deletePetById(petId:String, userId:String)

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun getPetsByUserId(userId: String): List<PetDto>
}


