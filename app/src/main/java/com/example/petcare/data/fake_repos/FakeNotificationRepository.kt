package com.example.petcare.data.fake_repos

import android.app.Notification
import com.example.petcare.domain.repository.INotificationRepository

class FakeNotificationRepository: INotificationRepository {
    override fun getFutureNotifications(): List<Notification> {
        TODO("Not yet implemented")
    }
}