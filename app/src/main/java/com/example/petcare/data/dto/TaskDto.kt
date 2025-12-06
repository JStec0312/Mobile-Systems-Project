package com.example.petcare.data.dto

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum

data class TaskDto(
    val id: String,
    val seriesId : String? = null,
    val petId : String,
    val type : taskTypeEnum? = taskTypeEnum.other,
    val title : String,
    val description : String? = null,
    val notes : String? = null,
    var status: taskStatusEnum,
    val priority: taskPriorityEnum? = taskPriorityEnum.normal,
    val createdAt: String? = null,
    val date : String? = null
)
