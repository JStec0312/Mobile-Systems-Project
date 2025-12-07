package com.example.petcare.data.dto

import com.example.petcare.common.notificationCategoryEnum
import kotlinx.datetime.LocalTime

data class ScheduledNotificationDto (
    val id: String,
    val userId: String,
    val parentEntityId: String,
    val category: notificationCategoryEnum,
    val scheduledAt: String,
    val isDelivered: Boolean = false,
    val title: String,
    val message: String,
)