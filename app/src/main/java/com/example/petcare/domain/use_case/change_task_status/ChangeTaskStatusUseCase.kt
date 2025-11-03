package com.example.petcare.domain.use_case.change_task_status

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.model.taskPriorityEnum
import com.example.petcare.domain.model.taskStatusEnum
import com.example.petcare.domain.model.taskTypeEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.ITaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class ChangeTaskStatusUseCase @Inject  constructor(
    private val taskRepository: ITaskRepository,
    private val currentUserProvider: IUserProvider,
    private val currentPetProvider: IPetProvider
){
    operator fun invoke(
        taskId: String,
        newStatus: taskStatusEnum,
        shouldFail: Boolean = false, //@NOTE Simulated failure
        delayMs: Long = 600, //@NOTE Simulated delay
    ): Flow<Resource<Task>> = flow {
        emit(Resource.Loading<Task>())
        delay(delayMs)
        if (shouldFail){
            emit(Resource.Error<Task>("Failed to change task status"))
        } else{
            val updatedTask = Task(
                id = UUID.randomUUID(),
                pet_id = currentPetProvider.getCurrentPetId(),
                type = taskTypeEnum.grooming,
                title = "mock title",
                notes = "mock notes",
                priority = taskPriorityEnum.high,
                status = newStatus,
                created_at = Clock.System.now()
            )
            emit(Resource.Success<Task>(updatedTask))
        }
    }
}