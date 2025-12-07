package com.example.petcare.domain.model

import com.example.petcare.common.notificationCategoryEnum
import kotlinx.datetime.LocalTime

data class ScheduledNotification (
    val id: String,
    val userId: String,
    val parentEntityId: String,
    val category: notificationCategoryEnum,
    val scheduledAt: LocalTime,
    val isDelivered: Boolean = false,
    val title: String,
    val message: String,
)