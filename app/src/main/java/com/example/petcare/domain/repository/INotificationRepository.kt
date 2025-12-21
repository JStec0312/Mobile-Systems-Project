package com.example.petcare.domain.repository

import android.app.Notification
import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.domain.model.NotificationSettings

interface INotificationRepository {
    suspend fun createNotificationChannelForNewUser(userId: String);
    suspend fun toggleNotificationSettingsForUser(userId: String, newCategory: notificationCategoryEnum);
    suspend fun isCategoryEnabledForUser(userId: String, category: notificationCategoryEnum): Boolean;
    suspend fun getForUser(userId: String): List<NotificationSettings>;


}