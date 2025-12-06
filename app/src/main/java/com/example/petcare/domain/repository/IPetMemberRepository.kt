package com.example.petcare.domain.repository

import com.example.petcare.domain.model.PetMember
import com.example.petcare.exceptions.Failure

interface IPetMemberRepository {
    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun addPetMember(petMemember: PetMember);

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun getPetIdsByUserId(userId: String): List<String>;

    @Throws (Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun isUserPetMember(userId: String, petId: String): Boolean;

}