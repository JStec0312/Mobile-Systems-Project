package com.example.petcare.data.dto.fake

import com.example.petcare.common.notificationCategoryEnum


data class NotificationSettingDto(
    val id: String,
    val userId: String,                          // /users/{uid}/notificationSettings/{channel} => opcjonalne w subkolekcji
    var category: notificationCategoryEnum,
    val updatedAt: String,
    var enabled: Boolean = true
)
