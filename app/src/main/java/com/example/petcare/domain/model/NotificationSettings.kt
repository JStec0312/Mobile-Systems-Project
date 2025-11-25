package com.example.petcare.domain.model

import com.example.petcare.common.notificationChannelEnum
import kotlinx.datetime.LocalDate

data class NotificationSettings(
    val id: String,
    val userId: String,
    val channel: notificationChannelEnum,
    val createdAt: LocalDate,
    val enabled: Boolean,
)
