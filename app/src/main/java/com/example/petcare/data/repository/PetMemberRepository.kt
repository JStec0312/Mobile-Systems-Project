package com.example.petcare.data.repository

import com.example.petcare.domain.model.PetMember
import com.example.petcare.domain.repository.IPetMemberRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class PetMemberRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
): IPetMemberRepository {
    override fun addPetMember(petMember: PetMember) {


    }

    override fun getPetIdsByUserId(userId: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun isUserPetMember(userId: String, petId: String): Boolean {
        TODO("Not yet implemented")
    }
}