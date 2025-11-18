package com.example.petcare.data.repository

import com.example.petcare.domain.repository.ITaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository(auth: FirebaseAuth, db: FirebaseFirestore) : ITaskRepository {
}