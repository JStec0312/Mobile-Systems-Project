package com.example.petcare.data.dto

import com.example.petcare.common.notificationCategoryEnum


data class NotificationSettingDto(
    val id: String,
    val userId: String,                          // /users/{uid}/notificationSettings/{channel} => opcjonalne w subkolekcji
    val category: notificationCategoryEnum,
    val enabled: Boolean,
    val createdAt: String,
)
