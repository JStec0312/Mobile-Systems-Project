package com.example.petcare.domain.repository

import android.app.Notification

interface INotificationRepository {
    fun getFutureNotifications(): List<Notification>
}