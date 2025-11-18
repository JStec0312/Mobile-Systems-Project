package com.example.petcare.data.repository

import com.example.petcare.domain.repository.IPetShareCodeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PetShareCodeRepository(db: FirebaseFirestore, auth: FirebaseAuth) : IPetShareCodeRepository {
}