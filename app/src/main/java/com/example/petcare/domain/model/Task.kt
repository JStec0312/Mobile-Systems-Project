package com.example.petcare.domain.model

import kotlinx.datetime.Instant
import java.util.UUID

data class Task(
    val id: UUID,
    val pet_id: UUID,
    val type: taskTypeEnum,
    val title: String,
    val notes: String?,
    val priority: taskPriorityEnum = taskPriorityEnum.normal,
    val status: taskStatusEnum = taskStatusEnum.planned,
    val created_at: Instant
)