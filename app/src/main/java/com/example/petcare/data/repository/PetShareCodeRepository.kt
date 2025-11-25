package com.example.petcare.data.repository

import com.example.petcare.data.dto.PetShareCodeDto
import com.example.petcare.domain.model.PetShareCode
import com.example.petcare.domain.repository.IPetShareCodeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PetShareCodeRepository(db: FirebaseFirestore, auth: FirebaseAuth) : IPetShareCodeRepository {
    override fun getPetShareCodeByValue(shareCode: String): PetShareCode? {
        TODO("Not yet implemented")
    }

    override fun deletePetShareCodeById(shareCodeId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun createPetShareCode(petShareCode: PetShareCode): PetShareCode {
        TODO("Not yet implemented")
    }
}