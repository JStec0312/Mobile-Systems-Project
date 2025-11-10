package com.example.petcare.data.dto

import com.example.petcare.common.notificationChannelEnum

data class NotificationSettingDto(
    val user_id: String,                          // /users/{uid}/notificationSettings/{channel} => opcjonalne w subkolekcji
    val channel: notificationChannelEnum = notificationChannelEnum.general,
    val enabled: Boolean = true
)