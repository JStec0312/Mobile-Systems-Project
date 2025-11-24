package com.example.petcare.data.fake_repos

import com.example.petcare.data.dto.TaskDto
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.repository.ITaskRepository

class FakeTaskRepository: ITaskRepository {
    private val tasks = mutableListOf<Task>()

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
        tasks.add(task)
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
        return tasks.filter { it.petId == petId }.map { it.toDto() }
    }

}