package com.example.petcare.domain.use_case.add_task

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.ITaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import kotlinx.datetime.Clock

class AddTaskUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider
) {
    operator fun invoke(
        type: taskTypeEnum,
        title: String,
        description: String?,
        priority: taskPriorityEnum,
        delayMs: Long = 600, // symulacja opóźnienia
        shouldFail: Boolean = false // symulacja błędu
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        delay(delayMs)

        if (shouldFail) {
            emit(Resource.Error("Failed to add task"))
            return@flow
        }

        val task = Task(
            id = UUID.randomUUID().toString(),
            petId = petProvider.getCurrentPetId(),
            type = type,
            title = title,
            notes = description,
            priority = priority,
            status = taskStatusEnum.planned,
            createdAt = Clock.System.now()
        )
        emit(Resource.Success(Unit))
    }
}