package com.example.petcare.data.fake_repos

import com.example.petcare.common.taskStatusEnum
import com.example.petcare.data.dto.TaskDto
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.repository.ITaskRepository

class FakeTaskRepository: ITaskRepository {
    private val tasks = mutableListOf<TaskDto>()

    override fun createTask(task: Task) {
        if (task.title == "Network Error") {
            throw com.example.petcare.exceptions.Failure.NetworkError()
        }
        if (task.title == "Server Error") {
            throw com.example.petcare.exceptions.Failure.ServerError()
        }
        if (task.title == "Unknown Error") {
            throw com.example.petcare.exceptions.Failure.UnknownError()
        }
        tasks.add(task.toDto())
    }

    override fun getTasksByPetId(petId: String): List<TaskDto> {
        if (petId == "Network Error") {
            throw com.example.petcare.exceptions.Failure.NetworkError()
        }
        if (petId == "Server Error") {
            throw com.example.petcare.exceptions.Failure.ServerError()
        }
        if (petId == "Unknown Error") {
            throw com.example.petcare.exceptions.Failure.UnknownError()
        }
        return tasks.filter { it.petId == petId }
    }
    override fun updateTaskStatus(taskId: String, newStatus: taskStatusEnum){
        if (taskId == "Network Error") {
            throw com.example.petcare.exceptions.Failure.NetworkError()
        }
        if (taskId == "Server Error") {
            throw com.example.petcare.exceptions.Failure.ServerError()
        }
        if (taskId == "Unknown Error") {
            throw com.example.petcare.exceptions.Failure.UnknownError()
        }
        val taskIndex = tasks.indexOfFirst { it.id == taskId}
        tasks[taskIndex].status = newStatus;
        return
    }

    override fun getTasksByPetIds(petIds: List<String>): List<TaskDto> {
        if (petIds.contains("Network Error")) {
            throw com.example.petcare.exceptions.Failure.NetworkError()
        }
        if (petIds.contains("Server Error")) {
            throw com.example.petcare.exceptions.Failure.ServerError()
        }
        if (petIds.contains("Unknown Error")) {
            throw com.example.petcare.exceptions.Failure.UnknownError()
        }
        return tasks.filter { petIds.contains(it.petId) }}

    override fun getTaskById(taskId: String): TaskDto {
        if (taskId == "Network Error") {
            throw com.example.petcare.exceptions.Failure.NetworkError()
        }
        if (taskId == "Server Error") {
            throw com.example.petcare.exceptions.Failure.ServerError()
        }
        if (taskId == "Unknown Error") {
            throw com.example.petcare.exceptions.Failure.UnknownError()
        }
        val task = tasks.find { it.id == taskId }
        if (task == null) {
            throw com.example.petcare.exceptions.GeneralFailure.TaskNotFound()
        }
        return task
    }
}



