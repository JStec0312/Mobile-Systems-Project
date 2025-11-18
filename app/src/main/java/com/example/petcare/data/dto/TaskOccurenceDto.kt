package com.example.petcare.data.dto

import com.example.petcare.common.frequencyEnum
import com.example.petcare.common.taskStatusEnum
import com.google.firebase.Timestamp

data class TaskOccurenceDto(
    val taskId: String,
    val status: taskStatusEnum,
    val completedAt: Timestamp? = null,
    val notes : String? = null,
)