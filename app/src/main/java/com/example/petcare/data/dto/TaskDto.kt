package com.example.petcare.data.dto

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskTypeEnum
import com.google.firebase.Timestamp

data class TaskDto(
    val type : taskTypeEnum? = taskTypeEnum.other,
    val title : String,
    val description : String? = null,
    val notes : String? = null,
    val dueAt : Timestamp? = null,
    val priorityEnum: taskPriorityEnum? = taskPriorityEnum.normal,
    val created_at: Timestamp? = null,

)