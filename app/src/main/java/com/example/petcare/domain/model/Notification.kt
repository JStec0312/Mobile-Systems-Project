package com.example.petcare.domain.model

import com.example.petcare.common.notificationCategoryEnum



data class Notification(
    val id: String,
    val userId: String,
    val category: String,
    val title: String,
    val time: Long,
    val type: notificationCategoryEnum
)