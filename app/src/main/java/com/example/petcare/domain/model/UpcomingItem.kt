package com.example.petcare.domain.model

import com.example.petcare.common.notificationCategoryEnum
import kotlinx.datetime.Instant

data class UpcomingItem(
    val id: String,
    val petId: String,
    val type: notificationCategoryEnum,
    val title: String,
    val at: Instant
)