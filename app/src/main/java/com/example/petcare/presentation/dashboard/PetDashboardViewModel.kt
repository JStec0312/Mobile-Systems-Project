package com.example.petcare.presentation.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.use_case.change_task_status.ChangeTaskStatusUseCase
import com.example.petcare.domain.use_case.get_pet_by_id.GetPetByIdUseCase
import com.example.petcare.domain.use_case.get_tasks.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class PetDashboardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTasksUseCase: GetTasksUseCase,
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(PetDashboardState())
    val state = _state.asStateFlow()

    init {
        val petId = savedStateHandle.get<String>("petId")
        if(petId != null) {
            loadDashboardData(petId)
        } else {
            _state.update { it.copy(error = "Pet ID not found") }
        }
    }

    private fun loadDashboardData(petId: String) {
        viewModelScope.launch {
            getPetByIdUseCase(petId).collect { petResource ->
                when (petResource) {
                    is Resource.Success -> {
                        _state.update { it.copy(pet = petResource.data) }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                error = petResource.message ?: "Could not load pet info",
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }

            getTasksUseCase()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    tasks = result.data ?: emptyList(),
                                    isLoading = false
                                )
                            }
                        }

                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    error = result.message ?: "An unexpected error occurred",
                                    isLoading = false
                                )
                            }
                        }

                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true) }
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }
    fun onTaskDone(task: Task) {
        val now = Clock.System.now()
        val newStatus = if(task.status == taskStatusEnum.done) {
            if (task.date < now) taskStatusEnum.skipped
            else taskStatusEnum.planned
        }
        else {
            taskStatusEnum.done
        }
        updateTaskStatus(task.id, newStatus)
    }

    fun onTaskCancelled(task: Task) {
        updateTaskStatus(task.id, taskStatusEnum.cancelled)
    }

    private fun updateTaskStatus(taskId: String, newStatus: taskStatusEnum) {
        viewModelScope.launch {
            val previousStatus = _state.value.tasks.find {
                it.id == taskId
            }?.status
            _state.update { currentState ->
                val updatedList = currentState.tasks.map {
                    if (it.id == taskId) {
                        it.copy(status = newStatus)
                    } else it
                }
                currentState.copy(
                    tasks = sortTasks(updatedList)
                    )
            }
            changeTaskStatusUseCase(taskId, newStatus).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        if (previousStatus != null) {
                            _state.update { errorState ->
                                val revertedList = errorState.tasks.map {
                                    if (it.id == taskId) it.copy(status = previousStatus)
                                    else it
                                }
                                errorState.copy(
                                    error = result.message ?: "Failed to update status",
                                    tasks = sortTasks(revertedList)
                                )
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
    private fun sortTasks(tasks: List<Task>): List<Task> {
        return tasks.sortedWith(
            compareBy<Task> { if (it.status == taskStatusEnum.cancelled) 1 else 0 }
                .thenBy { it.date }
        )
    }
}