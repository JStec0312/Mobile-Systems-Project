package com.example.petcare.data.dto.firestore

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.google.firebase.Timestamp

data class TaskFirestoreDto(
    val id: String = "",
    val seriesId: String? = null,
    val petId: String = "",
    val type: taskTypeEnum? = taskTypeEnum.other,
    val title: String = "",
    val description: String? = null,
    val notes: String? = null,
    var status: taskStatusEnum = taskStatusEnum.planned,
    val priority: taskPriorityEnum? = taskPriorityEnum.normal,
    val createdAt: Timestamp? = null,
    val date: Timestamp? = null,
    val rrule: String? = null,
    val isRecurring: Boolean = false,
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_PET_ID = "petId"
        const val FIELD_SERIES_ID = "seriesId"
        const val FIELD_DATE = "date"
        const val FIELD_CREATED_AT = "createdAt"
    }
}
