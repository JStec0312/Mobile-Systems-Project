package com.example.petcare.domain.model

import com.example.petcare.common.notificationChannelEnum
import java.util.UUID

data class NotificationSettings(
    val id: UUID,
    val pet_id: UUID?,
    val user_id: UUID,
    val channel: notificationChannelEnum,
    val created_at: kotlinx.datetime.Instant
)