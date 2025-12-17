package com.example.petcare.data.dto.firestore

import com.example.petcare.common.notificationCategoryEnum

data class NotificationFirestoreDto(
    val id: String = "",
    val userId: String = "",
    val category: String = "",
    val title: String = "",
    val time: Long = 0L,
    val type: notificationCategoryEnum = notificationCategoryEnum.tasks,
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_USER_ID = "userId"
        const val FIELD_TIME = "time"
        const val FIELD_CATEGORY = "category"
    }
}
