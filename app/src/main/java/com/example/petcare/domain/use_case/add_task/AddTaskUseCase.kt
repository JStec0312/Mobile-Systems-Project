package com.example.petcare.domain.use_case.add_task

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import kotlinx.datetime.LocalDate

class AddTaskUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider
) {
    operator fun invoke(
        petId: String,
        type: taskTypeEnum,
        title: String,
        notes: String,
        priority: taskPriorityEnum
    ): Flow<Resource<Task>> = flow {
        emit(Resource.Loading())
        try {
            val userId = userProvider.getUserId();
            if (userId == null) {
                emit(Resource.Error("User not logged in"))
                return@flow
            }
            val newTaskId = UUID.randomUUID().toString()

            val task = Task(
                id = newTaskId,
                petId = petId,
                type = type,
                title = title,
                notes = notes,
                priority = priority,
                status = taskStatusEnum.planned,
                createdAt = DateConverter.localDateNow()
            )
            taskRepository.createTask(task);
            emit(Resource.Success(task))
        } catch (e: Failure){
            emit(Resource.Error("An error occurred: ${e.message}"))
        }
    }
}