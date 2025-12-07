package com.example.petcare.domain.model

import com.example.petcare.common.notificationCategoryEnum
import kotlinx.datetime.LocalDate

data class NotificationSettings(
    val id: String,
    val userId: String,
    val category: notificationCategoryEnum,
    val updatedAt: LocalDate,
    val enabled: Boolean,
)
