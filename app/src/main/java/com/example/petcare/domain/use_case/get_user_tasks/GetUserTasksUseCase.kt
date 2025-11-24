package com.example.petcare.domain.use_case.get_user_tasks

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserTasksUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val userProvider: IUserProvider,
    private val memberRepository: IPetMemberRepository,
)  {
    operator fun invoke(): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading<List<Task>>())
        try{
            val userId = userProvider.getUserId();
            if (userId == null){
                emit(Resource.Error<List<Task>>("User not logged in"))
                return@flow
            }
            val petIds = memberRepository.getPetIdsByUserId(userId);
            val tasksDto = taskRepository.getTasksByPetIds(petIds);
            val tasks = tasksDto.map { it.toModel() };
            emit(Resource.Success<List<Task>>(tasks))
        } catch (e: Failure){
            emit(Resource.Error<List<Task>>("An error occurred: ${e.message}"))
        }
    }


}