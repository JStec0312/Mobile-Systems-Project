package com.example.petcare.domain.model

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import kotlinx.datetime.LocalDate

data class Task(
    val id: String,
    val petId: String,
    val type: taskTypeEnum?,
    val title: String,
    val notes: String?,
    val priority: taskPriorityEnum? = taskPriorityEnum.normal,
    val status: taskStatusEnum = taskStatusEnum.planned,
    val createdAt: LocalDate
)