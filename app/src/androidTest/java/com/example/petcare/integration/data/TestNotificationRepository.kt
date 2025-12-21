 package com.example.petcare.integration.data

import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.data.repository.FirestorePaths
import com.example.petcare.domain.repository.INotificationRepository
import com.example.petcare.exceptions.GeneralFailure
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class TestNotificationRepository {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    @Inject lateinit var firestore: FirebaseFirestore
    @Inject lateinit var notificationRepo: INotificationRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }



    @Test
    fun test_create_notification_channel_for_new_user() = runBlocking {
        val userId = "testUserId"
        notificationRepo.createNotificationChannelForNewUser(userId)

        val settings = notificationRepo.getForUser(userId)
        assertEquals(2, settings.size)
        assertTrue(settings.any { it.category == notificationCategoryEnum.meds && !it.enabled })
        assertTrue(settings.any { it.category == notificationCategoryEnum.tasks && !it.enabled })
    }

    @Test
    fun test_toggle_notification_settings_for_user() = runBlocking {
        val userId = "testUserId"
        notificationRepo.createNotificationChannelForNewUser(userId)

        // Initially disabled
        assertFalse(notificationRepo.isCategoryEnabledForUser(userId, notificationCategoryEnum.meds))

        // Toggle to enable
        notificationRepo.toggleNotificationSettingsForUser(userId, notificationCategoryEnum.meds)
        assertTrue(notificationRepo.isCategoryEnabledForUser(userId, notificationCategoryEnum.meds))

        // Toggle to disable
        notificationRepo.toggleNotificationSettingsForUser(userId, notificationCategoryEnum.meds)
        assertFalse(notificationRepo.isCategoryEnabledForUser(userId, notificationCategoryEnum.meds))
    }

    @Test
    fun test_is_category_enabled_for_user_throws_not_found() = runBlocking {
        val userId = "nonExistentUser"
        try {
            notificationRepo.isCategoryEnabledForUser(userId, notificationCategoryEnum.meds)
            fail("Should have thrown NotificationSettingNotFound exception")
        } catch (_: GeneralFailure.NotificationSettingNotFound) {
            // Expected
        }
    }

    @Test
    fun test_toggle_notification_settings_throws_not_found() = runBlocking {
        val userId = "nonExistentUser"
        try {
            notificationRepo.toggleNotificationSettingsForUser(userId, notificationCategoryEnum.meds)
            fail("Should have thrown NotificationSettingNotFound exception")
        } catch (_: GeneralFailure.NotificationSettingNotFound) {
            // Expected
        }
    }
}

