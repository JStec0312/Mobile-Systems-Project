package com.example.petcare.domain.repository

import com.example.petcare.data.dto.TaskDto
import com.example.petcare.domain.model.Task
import com.example.petcare.exceptions.Failure

interface ITaskRepository {
    @Throws (Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun createTask(task: Task)

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun getTasksByPetId(petId: String): List<TaskDto>

}