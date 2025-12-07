package com.example.petcare.data.repository

import com.example.petcare.domain.repository.INotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationRepository(auth: FirebaseAuth, db: FirebaseFirestore) : INotificationRepository {
}