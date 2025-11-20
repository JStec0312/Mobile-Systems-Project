package com.example.petcare.domain.repository

import com.example.petcare.domain.model.PetMember
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure

interface IPetMemberRepository {
    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun addPetMember(petMemember: PetMember);
}