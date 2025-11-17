package com.example.petcare.domain.model

import com.example.petcare.common.notificationChannelEnum
import java.util.UUID

data class NotificationSettings(
    val id: UUID,
    val petId: UUID?,
    val userId: UUID,
    val channel: notificationChannelEnum,
    val createdAt: kotlinx.datetime.Instant
)