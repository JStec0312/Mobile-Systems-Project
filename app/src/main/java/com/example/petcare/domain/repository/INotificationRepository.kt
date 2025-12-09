package com.example.petcare.domain.repository

import android.app.Notification
import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.domain.model.NotificationSettings

interface INotificationRepository {
    fun createNotificationChannelForNewUser(userId: String);
    fun toggleNotificationSettingsForUser(userId: String, newCategory: notificationCategoryEnum);
    fun isCategoryEnabledForUser(userId: String, category: notificationCategoryEnum): Boolean;
    fun getForUser(userId: String): List<NotificationSettings>;


}