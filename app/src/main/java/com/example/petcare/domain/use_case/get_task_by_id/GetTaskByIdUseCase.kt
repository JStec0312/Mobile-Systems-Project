package com.example.petcare.domain.use_case.get_task_by_id
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import javax.inject.Inject
class GetTaskByIdUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val taskRepository: ITaskRepository
) {
    operator fun invoke(
        taskId: String,
    ): Flow<Resource<Task>> = flow {
       emit(Resource.Loading())
         try {
             val userId: String? = userProvider.getUserId();
                if (userId==null){
                    emit(Resource.Error("User not logged in"))
                    return@flow
                }
                val taskDto = taskRepository.getTaskById(taskId);
                val task = taskDto.toModel();
                emit(Resource.Success(task))
         } catch (e: Failure){
                emit(Resource.Error(e.message));
         } catch (e: GeneralFailure.TaskNotFound){
                emit(Resource.Error("Task not found"));
         }
    }
}