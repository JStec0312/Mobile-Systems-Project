package com.example.petcare.domain.model

import com.example.petcare.common.notificationChannelEnum
import kotlinx.datetime.Instant

data class NotificationSettings(
    val id: String,
    val petId: String?,
    val userId: String?,
    val channel: notificationChannelEnum,
    val createdAt: Instant
)