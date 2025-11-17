package com.example.petcare.domain.model

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import kotlinx.datetime.Instant
import java.util.UUID

data class Task(
    val id: UUID,
    val petId: UUID,
    val type: taskTypeEnum,
    val title: String,
    val notes: String?,
    val priority: taskPriorityEnum = taskPriorityEnum.normal,
    val status: taskStatusEnum = taskStatusEnum.planned,
    val createdAt: Instant
)