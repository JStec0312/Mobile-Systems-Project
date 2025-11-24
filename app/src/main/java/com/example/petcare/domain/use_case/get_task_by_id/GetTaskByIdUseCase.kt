package com.example.petcare.domain.use_case.get_task_by_id
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import javax.inject.Inject
class GetTaskByIdUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository
) {
    operator fun invoke(
        taskId: String,
        delayMs: Long = 600, //@NOTE Simulated delay
        shouldFail : Boolean = false, //@NOTE Simulated failure
    ): Flow<Resource<Task>> = flow {
        emit(Resource.Loading<Task>())
        delay(delayMs)
        if (shouldFail){
            emit(Resource.Error<Task>("Failed to get task by id"))

        }
        val petId = petProvider.getCurrentPetId()
        if (petId==null){
            emit(Resource.Error<Task>("No pet selected"))
        }
        else{
            val task = Task(
                id = taskId,
                petId = petId,
                type = taskTypeEnum.grooming,
                title = "Grooming Session",
                notes = "Remember to brush the fur thoroughly",
                priority = taskPriorityEnum.high,
                status = taskStatusEnum.skipped,
                createdAt = LocalDate(11, 12, 2003),
            )
            emit(Resource.Success<Task>(task))
        }
    }
}