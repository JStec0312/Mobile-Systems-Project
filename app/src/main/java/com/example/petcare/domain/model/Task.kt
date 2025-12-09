package com.example.petcare.domain.model

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class Task(
    val id: String,
    val seriesId : String? = null,
    val petId: String,
    val type: taskTypeEnum?,
    val title: String,
    val notes: String?,
    val priority: taskPriorityEnum? = taskPriorityEnum.normal,
    var status: taskStatusEnum = taskStatusEnum.planned,
    val createdAt: LocalDate,
    val date: Instant,
    var rrule: String? = null,
)
