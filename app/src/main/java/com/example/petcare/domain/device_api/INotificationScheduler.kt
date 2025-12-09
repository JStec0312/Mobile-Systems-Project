package com.example.petcare.domain.device_api

import com.example.petcare.common.notificationCategoryEnum

interface INotificationScheduler {
    fun scheduleInitialNotification(
        entityId: String,
        type: notificationCategoryEnum, // Enum: MED, TASK
        scheduledTime: Long
    )
    fun cancelNotification(entityId: String)
}