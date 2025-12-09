package com.example.petcare.data.repository

import android.app.Notification
import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.domain.model.NotificationSettings
import com.example.petcare.domain.repository.INotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationRepository(auth: FirebaseAuth, db: FirebaseFirestore) : INotificationRepository {


    override fun createNotificationChannelForNewUser(userId: String) {
        TODO("Not yet implemented")
    }

    override fun toggleNotificationSettingsForUser(
        userId: String,
        newCategory: notificationCategoryEnum
    ) {
        TODO("Not yet implemented")
    }

    override fun isCategoryEnabledForUser(
        userId: String,
        category: notificationCategoryEnum
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun getForUser(userId: String): List<NotificationSettings> {
        TODO("Not yet implemented")
    }
}