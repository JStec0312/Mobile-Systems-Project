package com.example.petcare.data.repository

import android.app.Notification
import com.example.petcare.domain.repository.INotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationRepository(auth: FirebaseAuth, db: FirebaseFirestore) : INotificationRepository {
    override fun getFutureNotifications(): List<Notification> {
        TODO("Not yet implemented")
    }
}