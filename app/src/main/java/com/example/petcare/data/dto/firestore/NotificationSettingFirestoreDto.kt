package com.example.petcare.data.dto.firestore

import com.example.petcare.common.notificationCategoryEnum
import com.google.firebase.Timestamp


data class NotificationSettingFirestoreDto(
    val id: String = "",
    val userId: String = "",
    var category: notificationCategoryEnum = notificationCategoryEnum.tasks,
    val updatedAt: Timestamp = Timestamp.now(),
    var enabled: Boolean = true,
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_USER_ID = "userId"
        const val FIELD_CATEGORY = "category"
        const val FIELD_UPDATED_AT = "updatedAt"
    }
}
