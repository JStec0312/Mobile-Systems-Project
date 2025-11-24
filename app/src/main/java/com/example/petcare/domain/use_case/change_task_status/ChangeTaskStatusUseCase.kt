package com.example.petcare.domain.use_case.change_task_status

import com.example.petcare.common.Resource
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.exceptions.Failure
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
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        try{
            val userId = currentUserProvider.getUserId();
            if (userId == null){
                emit(Resource.Error<Unit>("User not logged in"))
                return@flow
            }

            taskRepository.updateTaskStatus(taskId, newStatus);
            emit(Resource.Success<Unit>(Unit))
        } catch(e: Failure){
            emit(Resource.Error<Unit>("An error occurred: ${e.message}"))
        }
    }
}
