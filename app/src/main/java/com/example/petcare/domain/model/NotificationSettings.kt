package com.example.petcare.domain.model

import com.example.petcare.common.notificationChannelEnum
import com.example.petcare.data.dto.NotificationSettingDto
import kotlinx.datetime.LocalDate

data class NotificationSettings(
    val id: String,
    val userId: String,
    val channel: notificationChannelEnum,
    val createdAt: LocalDate,
    val enabled: Boolean,
){
    fun toDto(): NotificationSettingDto{
        return NotificationSettingDto(
            id = this.id,
            userId = this.userId,
            channel = this.channel,
            createdAt = this.createdAt.toString(),
            enabled = this.enabled
        )
    }
}