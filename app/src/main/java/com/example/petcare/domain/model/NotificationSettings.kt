package com.example.petcare.domain.model

import com.example.petcare.common.notificationCategoryEnum
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class NotificationSettings(
    val id: String,
    val userId: String,
    val category: notificationCategoryEnum,
    val updatedAt: Instant,
    var enabled: Boolean,
)
