package com.example.petcare.domain.use_case.edit_task

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.providers.implementation.PetProvider
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.ITaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant


class EditTaskUseCase  @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val userProvider: UserProvider,
    private val petProvider: PetProvider,
    private val petMemberRepository: IPetMemberRepository
) {
    operator fun invoke(
        taskId: String,
        //to dodaje bo nie dzialalo
        petId: String?,
        seriesId: String? = null,
        type: taskTypeEnum? = null,
        title: String?,
        notes: String? = null,
        priority: taskPriorityEnum? = null,
        date: Instant? = null,
        editWholeSeries: Boolean = false,
    ) : Flow<Resource<Task>> = flow {
        emit(Resource.Loading())
        try {
            val currentUser = userProvider.getUserId()
            // val currentPet = petProvider.getPetId()
            val currentPet = petId ?: ""
            if (currentUser==null) {
                emit(Resource.Error("User is not set."))
                return@flow
            }
            val isMember = petMemberRepository.isUserPetMember(
                userId = currentUser,
                petId = currentPet
            )
            if (!isMember) {
                emit(Resource.Error("User does not have permission to edit tasks for this pet."))
                return@flow
            }

            val existingTask = taskRepository.getTaskById(taskId)

            val updatedTask = existingTask.copy(
                seriesId = seriesId ?: existingTask.seriesId,
                type = type ?: existingTask.type,
                title = title ?: existingTask.title,
                notes = notes ?: existingTask.notes,
                priority = priority ?: existingTask.priority,
                date = date ?: existingTask.date
                //seriesId = if (editWholeSeries) seriesId ?: existingTask.seriesId else existingTask.seriesId,
               // type = if (editWholeSeries) type ?: existingTask.type else existingTask.type,
              //  title = title ?: existingTask.title,
               // notes = if (editWholeSeries) notes ?: existingTask.notes else existingTask.notes,
               // priority = if (editWholeSeries) priority ?: existingTask.priority else existingTask.priority,
              //  date = if (editWholeSeries) date ?: existingTask.date else existingTask.date
            )
            taskRepository.updatateTask(updatedTask, editWholeSeries)

            emit(Resource.Success(updatedTask))
        } catch (e: GeneralFailure.TaskNotFound){
            emit(Resource.Error("Task not found"))

        }
        catch (e: Failure) {
                emit(Resource.Error("An error occurred while editing the task: ${e.message}"))
            }
        }
    }
