package com.example.petcare.data.repository

import com.example.petcare.common.taskStatusEnum
import com.example.petcare.data.dto.TaskDto
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.repository.ITaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository(auth: FirebaseAuth, db: FirebaseFirestore) : ITaskRepository {
    override fun createTask(task: Task) {
        TODO("Not yet implemented")
    }

    override fun getTasksByPetId(petId: String): List<TaskDto> {
        TODO("Not yet implemented")
    }

    override fun updateTaskStatus(
        taskId: String,
        newStatus: taskStatusEnum
    ) {
        TODO("Not yet implemented")
    }

    override fun getTasksByPetIds(petIds: List<String>): List<TaskDto> {
        TODO("Not yet implemented")
    }

    override fun getTaskById(taskId: String): TaskDto {
        TODO("Not yet implemented")
    }
}