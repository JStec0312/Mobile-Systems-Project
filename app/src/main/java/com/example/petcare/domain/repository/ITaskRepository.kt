package com.example.petcare.domain.repository

import com.example.petcare.domain.model.Task
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure

interface ITaskRepository {
    @Throws (Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun createTask(task: Task)

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun getTasksByPetId(petId: String): List<Task>

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    fun updateTaskStatus(taskId: String, newStatus: com.example.petcare.common.taskStatusEnum)

    @Throws
    fun getTasksByPetIds(petIds: List<String>): List<Task>

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class, GeneralFailure.TaskNotFound::class)
    fun getTaskById(taskId: String): Task
}