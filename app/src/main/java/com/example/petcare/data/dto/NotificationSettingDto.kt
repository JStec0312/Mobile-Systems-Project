package com.example.petcare.data.dto

import com.example.petcare.common.notificationChannelEnum

data class NotificationSettingDto(
    val id: String,
    val userId: String,                          // /users/{uid}/notificationSettings/{channel} => opcjonalne w subkolekcji
    val channel: notificationChannelEnum = notificationChannelEnum.general,
    val enabled: Boolean,
    val createdAt: String,
)
