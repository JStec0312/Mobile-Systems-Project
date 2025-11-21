package com.example.petcare.data.dto

import com.example.petcare.common.notificationChannelEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.NotificationSettings

data class NotificationSettingDto(
    val id: String,
    val userId: String,                          // /users/{uid}/notificationSettings/{channel} => opcjonalne w subkolekcji
    val channel: notificationChannelEnum = notificationChannelEnum.general,
    val enabled: Boolean,
    val createdAt: String,

){
    fun toModel(): NotificationSettings{
        return NotificationSettings(
            id = this.id ?: "",
            userId = this.userId,
            channel = this.channel,
            createdAt = DateConverter.stringToLocalDate(this.createdAt),
            enabled = this.enabled
        )
    }
}