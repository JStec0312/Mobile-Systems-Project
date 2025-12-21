package com.example.petcare.domain.repository

import com.example.petcare.domain.model.Task
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure

interface ITaskRepository {
    @Throws (Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun createTask(task: Task, rrule: String?)

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun getTasksByPetId(petId: String): List<Task>

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend  fun updateTaskStatus(taskId: String, newStatus: com.example.petcare.common.taskStatusEnum)

    @Throws
    suspend  fun getTasksByPetIds(petIds: List<String>): List<Task>

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class, GeneralFailure.TaskNotFound::class)
    suspend  fun getTaskById(taskId: String): Task

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun deleteTaskById(task: Task, deleteWholeSeries: Boolean)

    @Throws(Failure.NetworkError::class, Failure.ServerError::class, Failure.UnknownError::class)
    suspend fun updatateTask(task: Task, updateWholeSeries: Boolean)

    suspend  fun getTasksByPetIdsInDateRange(petIds: List<String>, from: kotlinx.datetime.Instant, to: kotlinx.datetime.Instant): List<Task>
}

