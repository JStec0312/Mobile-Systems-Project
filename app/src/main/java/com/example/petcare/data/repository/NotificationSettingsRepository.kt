package com.example.petcare.data.repository

import com.example.petcare.domain.repository.INotificationSettingsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationSettingsRepository(auth: FirebaseAuth, db: FirebaseFirestore) : INotificationSettingsRepository {
}