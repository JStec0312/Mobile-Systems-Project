package com.example.petcare.data.repository

import com.example.petcare.domain.repository.IMedicationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MedicationRepository(auth: FirebaseAuth, db: FirebaseFirestore) : IMedicationRepository {
}