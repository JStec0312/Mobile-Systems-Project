package com.example.petcare.data.dto

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.domain.model.Task
import com.google.firebase.Timestamp

data class TaskDto(
    val id: String,
    val type : taskTypeEnum? = taskTypeEnum.other,
    val title : String,
    val description : String? = null,
    val notes : String? = null,
    val dueAt : String? = null,
    val priorityEnum: taskPriorityEnum? = taskPriorityEnum.normal,
    val createdAt: String? = null,
) {
    fun toModel(): Task{
        return Task(
            id = TODO,
            petId = TODO(),
            type = TODO(),
            title = TODO(),
            notes = TODO(),
            priority = TODO(),
            status = TODO(),
            createdAt = TODO()
        )
    }
}