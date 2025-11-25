package com.example.petcare.domain.model

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.data.dto.TaskDto
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class Task(
    val id: String,
    val petId: String,
    val type: taskTypeEnum?,
    val title: String,
    val notes: String?,
    val priority: taskPriorityEnum? = taskPriorityEnum.normal,
    var status: taskStatusEnum = taskStatusEnum.planned,
    val createdAt: LocalDate,
    val date: Instant
) {
    fun toDto(): TaskDto{
        return TaskDto(
            id = this.id,
            petId = this.petId,
            type = this.type,
            title = this.title,
            notes = this.notes,
            priority = this.priority,
            status = this.status,
            createdAt = this.createdAt.toString(),
            date = this.date.toString()
        )
    }
}