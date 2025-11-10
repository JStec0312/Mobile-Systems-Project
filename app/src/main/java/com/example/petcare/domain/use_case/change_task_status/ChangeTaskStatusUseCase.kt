package com.example.petcare.domain.use_case.change_task_status

import com.example.petcare.common.Resource
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.ITaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChangeTaskStatusUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val currentUserProvider: IUserProvider,
    private val currentPetProvider: IPetProvider
){
    operator fun invoke(
        taskId: String,
        newStatus: taskStatusEnum,
        shouldFail: Boolean = false,
        delayMs: Long = 600,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        delay(delayMs)

        if (shouldFail) {
            emit(Resource.Error("Failed to change task status"))
        }
        emit(Resource.Success(Unit)) // mock: sygnał sukcesu bez danych
    }
}
