package com.example.petcare.data.repository

import com.example.petcare.common.taskStatusEnum
import com.example.petcare.data.dto.TaskDto
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.repository.ITaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository(auth: FirebaseAuth, db: FirebaseFirestore) : ITaskRepository {
    override fun createTask(task: Task, rrule: String?) {

    }

    override fun getTasksByPetId(petId: String): List<Task> {
        TODO("Not yet implemented")
    }

    override fun updateTaskStatus(
        taskId: String,
        newStatus: taskStatusEnum
    ) {
        TODO("Not yet implemented")
    }

    override fun getTasksByPetIds(petIds: List<String>): List<Task> {
        TODO("Not yet implemented")
    }

    override fun getTaskById(taskId: String): Task {
        TODO("Not yet implemented")
    }

    override fun deleteTaskById(
        task: Task,
        deleteWholeSeries: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun updatateTask(
        task: Task,
        updateWholeSeries: Boolean
    ) {
        TODO("Not yet implemented")
    }


}