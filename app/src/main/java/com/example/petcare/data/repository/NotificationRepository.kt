package com.example.petcare.data.repository

import android.app.Notification
import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.data.dto.fake.NotificationSettingDto
import com.example.petcare.data.dto.firestore.NotificationSettingFirestoreDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.domain.model.NotificationSettings
import com.example.petcare.domain.repository.INotificationRepository
import com.example.petcare.exceptions.GeneralFailure
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import java.util.UUID

class NotificationRepository(auth: FirebaseAuth, db: FirebaseFirestore) : INotificationRepository {
    private val col = db.collection(FirestorePaths.USER_NOTIFICATION_SETTINGS);
    override suspend  fun createNotificationChannelForNewUser(userId: String) {
        val newNotificationSetting = NotificationSettingFirestoreDto(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = notificationCategoryEnum.meds,
            enabled = false
        );
        val newNotificationSetting2 = NotificationSettingFirestoreDto(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = notificationCategoryEnum.tasks,
            enabled = false
        );
        try{
            col.document(newNotificationSetting.id).set(newNotificationSetting).await()
            col.document(newNotificationSetting2.id).set(newNotificationSetting2).await()

        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "createNotificationChannelForNewUser")
        }

    }

    override suspend fun toggleNotificationSettingsForUser(
        userId: String,
        newCategory: notificationCategoryEnum
    ) {
        try {
            val querySnapshot = col
                .whereEqualTo("userId", userId)
                .whereEqualTo("category", newCategory.name)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                // UPSERT: create
                val newDocRef = col.document()
                val newSetting = NotificationSettingFirestoreDto(
                    id = newDocRef.id,
                    userId = userId,
                    category = newCategory,
                    enabled = true
                )
                newDocRef.set(newSetting).await()
                return
            }
            val doc = querySnapshot.documents.first()
            val current = doc.toObject(NotificationSettingFirestoreDto::class.java)

            val currentEnabled = current?.enabled ?: false
            doc.reference.update("enabled", !currentEnabled).await()
        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "toggleNotificationSettingsForUser")
        }
    }


    override suspend fun isCategoryEnabledForUser(
        userId: String,
        category: notificationCategoryEnum
    ): Boolean {
        try {
            val querySnapshot = col
                .whereEqualTo("userId", userId)
                .whereEqualTo("category", category.name)
                .get()
                .await()
            if (querySnapshot.isEmpty) {
                throw GeneralFailure.NotificationSettingNotFound("Notification settings not found for user $userId and category $category")
            }
            val doc = querySnapshot.documents.first()
            val setting = doc.toObject(NotificationSettingFirestoreDto::class.java)
            if (setting == null) {
                throw GeneralFailure.DataCorruption()
            }
            return setting.enabled
        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "isCategoryEnabledForUser")

        }
    }

    override suspend fun getForUser(userId: String): List<NotificationSettings> {
        try{
            val querySnapshot = col
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val result = querySnapshot.documents.map { it.toObject(NotificationSettingFirestoreDto::class.java) }
            return result.filterNotNull().map { it.toDomain() }
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "getForUser")
        }
    }
}