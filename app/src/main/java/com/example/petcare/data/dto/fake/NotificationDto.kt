package com.example.petcare.data.dto.fake

import com.example.petcare.common.notificationCategoryEnum

data class NotificationDto(
    val id: String,
    val userId: String,
    val category: String,
    val title: String,
    val time: Long,
    val type: notificationCategoryEnum
)