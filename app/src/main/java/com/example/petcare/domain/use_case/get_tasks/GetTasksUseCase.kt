package com.example.petcare.domain.use_case.get_tasks
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.ITaskRepository
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
    operator fun invoke(
        petId: UUID,
        delayMs: Long = 600,
        shouldFail : Boolean = false,
    ): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading<List<Task>>())
        delay(delayMs)
        if (shouldFail){
            emit(Resource.Error<List<Task>>("Failed to get tasks"))

        } else{
            val tasks = listOf(
                Task(
                    id = UUID.randomUUID(),
                    pet_id = petProvider.getCurrentPetId(),
                    type = taskTypeEnum.training,
                    title = "Training Session",
                    notes = "Practice basic commands",
                    priority = taskPriorityEnum.low,
                    status = taskStatusEnum.planned,
                    created_at = Instant.parse("2023-10-01T10:00:00Z")
                ),
                Task(
                    id = UUID.randomUUID(),
                    pet_id = petProvider.getCurrentPetId(),
                    type = taskTypeEnum.walk,
                    title = "Morning Walk",
                    notes = "30-minute walk in the park",
                    priority = taskPriorityEnum.normal,
                    status = taskStatusEnum.planned,
                    created_at = Instant.parse("2023-10-02T08:00:00Z")
                )
            )
            emit(Resource.Success<List<Task>>(tasks))
        }
    }
}
