package com.example.petcare.domain.use_case.add_task

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
        delayMs: Long = 600, //@NOTE Simulated delay
        shouldFail: Boolean = false //@NOTE Simulated failure
    ): Flow<Resource<Task>> = flow {
        emit(Resource.Loading<Task>());
        delay(delayMs);
        if (shouldFail){
            emit(Resource.Error("Failed to add task"))
        } else{
            val task = Task(
                id = UUID.randomUUID(),
                pet_id = petProvider.getCurrentPetId(),
                type = type,
                title = title,
                notes = description,
                priority = priority,
                status = taskStatusEnum.planned,
                created_at = Clock.System.now()
            )
        }
    }

}