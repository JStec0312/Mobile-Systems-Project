package com.example.petcare.domain.use_case.delete_task

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val userProviderImpl: UserProvider,
    private val taskRepository: ITaskRepository,
    private val petMemberRepository: IPetMemberRepository
) {
    operator fun invoke(
        taskId: String,
        deleteWholeSeries: Boolean
    ): Flow<Resource<Unit>> = flow {
        try {
            val userId = userProvider.getUserId();
            if (userId == null) {
                emit(Resource.Error("User not logged in"))
                return@flow
            }
            val task = taskRepository.getTaskById(taskId)
            val isMember = petMemberRepository.isUserPetMember(userId, task.petId)
            if (!isMember) {
                emit(Resource.Error("User does not have permission to delete this task"))
                return@flow
            }


            taskRepository.deleteTaskById(task, deleteWholeSeries);
            emit(Resource.Success(Unit));

        } catch (e: Failure){
            emit(Resource.Error("An error occurred: ${e.message}"))
        } catch (e: GeneralFailure.TaskNotFound){
            emit(Resource.Error("Task not found"))
        }
    }
}