package com.example.petcare.data.dto

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.Task

data class TaskDto(
    val id: String,
    val petId : String,
    val type : taskTypeEnum? = taskTypeEnum.other,
    val title : String,
    val description : String? = null,
    val notes : String? = null,
    var status: taskStatusEnum,
    val dueAt : String? = null,
    val priority: taskPriorityEnum? = taskPriorityEnum.normal,
    val createdAt: String? = null,
    val date : String? = null
) {
    fun toModel(): Task{
        return Task(
            id = this.id,
            petId = this.petId,
            type = this.type,
            title = this.title,
            notes = this.notes,
            priority = this.priority,
            status = this.status,
            createdAt = DateConverter.stringToLocalDate(this.createdAt),
            date = DateConverter.stringToInstant(this.date)
        )
    }
}