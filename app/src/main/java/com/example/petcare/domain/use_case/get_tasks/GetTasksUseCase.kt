package com.example.petcare.domain.use_case.get_tasks
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import javax.inject.Inject

class  GetTasksUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: ITaskRepository
) {
    operator fun invoke(): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading<List<Task>>())
        try{
            val petId = petProvider.getCurrentPetId()
            if (petId==null){
                emit(Resource.Error<List<Task>>("No pet selected"))
                return@flow
            }
            val tasks = petRepository.getTasksByPetId(petId)
            emit(Resource.Success<List<Task>>(tasks))
        } catch (e: Failure){
            emit(Resource.Error<List<Task>>("An error occurred: ${e.message}"))
        }
    }
}
