package com.example.petcare.data.repository

import com.example.petcare.domain.repository.IWalkRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WalkRepository(auth: FirebaseAuth, db: FirebaseFirestore) : IWalkRepository{
}