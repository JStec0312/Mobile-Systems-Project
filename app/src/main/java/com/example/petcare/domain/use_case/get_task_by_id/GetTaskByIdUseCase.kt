package com.example.petcare.domain.use_case.get_task_by_id
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.model.sexEnum
import com.example.petcare.domain.model.speciesEnum
import com.example.petcare.domain.model.taskPriorityEnum
import com.example.petcare.domain.model.taskStatusEnum
import com.example.petcare.domain.model.taskTypeEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.INotificationSettingsRepository
import com.example.petcare.domain.repository.IPetRepository
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import java.util.UUID
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
        taskId: UUID,
        delayMs: Long = 600, //@NOTE Simulated delay
        shouldFail : Boolean = false, //@NOTE Simulated failure
    ): Flow<Resource<Task>> = flow {
        emit(Resource.Loading<Task>())
        delay(delayMs)
        if (shouldFail){
            emit(Resource.Error<Task>("Failed to get task by id"))

        } else{
            val task = Task(
                id = taskId,
                pet_id = petProvider.getCurrentPetId(),
                type = taskTypeEnum.grooming,
                title = "Grooming Session",
                notes = "Remember to brush the fur thoroughly",
                priority = taskPriorityEnum.high,
                status = taskStatusEnum.skipped,
                created_at = Instant.parse("2023-10-01T10:00:00Z")
            )
            emit(Resource.Success<Task>(task))
        }
    }
}