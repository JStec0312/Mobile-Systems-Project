package com.example.petcare.data.fake_repos

import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.config.DeveloperSettings
import com.example.petcare.data.dto.fake.NotificationDto
import com.example.petcare.data.dto.fake.NotificationSettingDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.domain.model.NotificationSettings

import com.example.petcare.domain.repository.INotificationRepository
import kotlinx.datetime.Clock
import java.util.UUID

class FakeNotificationRepository: INotificationRepository {
    private val usersNotificationSettings = mutableListOf<NotificationSettingDto>();
    private val scheduledNotifications = mutableListOf<NotificationDto>();

    init{
        val fakeNotificationSettingMeds = NotificationSettingDto(
            id = UUID.randomUUID().toString(),
            userId = DeveloperSettings.TEST_USER_ID,
            category = notificationCategoryEnum.meds,
            updatedAt = Clock.System.now().toString(),
            enabled = true
        );
        val fakeNotificationSettingTasks = NotificationSettingDto(
            id = UUID.randomUUID().toString(),
            userId = DeveloperSettings.TEST_USER_ID,
            category = notificationCategoryEnum.tasks,
            updatedAt = Clock.System.now().toString(),
            enabled = true
        );
        usersNotificationSettings.add(fakeNotificationSettingMeds);
        usersNotificationSettings.add(fakeNotificationSettingTasks);
    }
    override fun createNotificationChannelForNewUser(userId: String) {
        val newNotificationSetting = NotificationSettingDto(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = notificationCategoryEnum.meds,
            updatedAt = Clock.System.now().toString(),
            enabled = true
        );
        val newNotificationSetting2 = NotificationSettingDto(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = notificationCategoryEnum.tasks,
            updatedAt = Clock.System.now().toString(),
            enabled = false
        );
        usersNotificationSettings.add(newNotificationSetting);
        usersNotificationSettings.add(newNotificationSetting2);


    }

    override fun toggleNotificationSettingsForUser(
        userId: String,
        category: notificationCategoryEnum,
    ) {
        val settingIdx = usersNotificationSettings.indexOfFirst {
            it.userId == userId && it.category == category
        }
        if (settingIdx != -1){
            usersNotificationSettings[settingIdx].enabled=!usersNotificationSettings[settingIdx].enabled;
        }
        return
    }

    override fun isCategoryEnabledForUser(
        userId: String,
        category: notificationCategoryEnum
    ): Boolean {
        val setting = usersNotificationSettings.find {
            it.userId == userId && it.category == category
        }
        return setting?.enabled ?: false;
    }
    override fun getForUser(userId: String): List<NotificationSettings> {
        return  usersNotificationSettings.filter { it.userId == userId }.map{it.toDomain()}
    }

}
