package com.example.petcare.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.use_case.change_task_status.ChangeTaskStatusUseCase
import com.example.petcare.domain.use_case.delete_task.DeleteTaskUseCase
import com.example.petcare.domain.use_case.get_pets.GetPetsUseCase
import com.example.petcare.domain.use_case.get_user_tasks.GetUserTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getUserTasksUseCase: GetUserTasksUseCase,
    private val getPetsUseCase: GetPetsUseCase,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CalendarState())

    val state: StateFlow<CalendarState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getUserTasksUseCase(),
                getPetsUseCase()
            ) { tasksResource, petResource ->
                Pair(tasksResource, petResource)
            }.collect { (tasksResource, petResource) ->
                _state.update { currentState ->
                    val isLoading =
                        tasksResource is Resource.Loading || petResource is Resource.Loading
                    val error = tasksResource.message ?: petResource.message

                    val newAllTasks = tasksResource.data ?: currentState.allUserTasks
                    val newAllPets = petResource.data ?: currentState.allPets

                    calculateNewState(
                        baseState = currentState.copy(
                            allUserTasks = newAllTasks,
                            allPets = newAllPets,
                            isLoading = isLoading,
                            error = error
                        )
                    )
                }
            }
        }
    }

    private fun calculateNewState(baseState: CalendarState): CalendarState {
        val filteredTasks = if (baseState.selectedPetsIds.isEmpty()) {
            baseState.allUserTasks
        } else {
            baseState.allUserTasks.filter { it.petId in baseState.selectedPetsIds }
        }

        val tasksMapped = filteredTasks.groupBy { task ->
            task.date.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }

        val updatedSelectedDayTasks = if (baseState.selectedDate != null) {
            tasksMapped[baseState.selectedDate] ?: emptyList()
        } else {
            baseState.selectedDayTasks
        }

        return baseState.copy(
            tasksByDate = tasksMapped,
            selectedDayTasks = updatedSelectedDayTasks
        )
    }

    fun onMonthChange(newMonth: YearMonth) {
        _state.value = _state.value.copy(
            currentMonth = newMonth
        )
    }

    fun onDayClick(date: LocalDate) {
        val tasksForDay = state.value.tasksByDate[date] ?: emptyList()
        _state.update {
            it.copy(
                activateBottomSheet = CalendarBottomSheetType.DayDetails,
                selectedDate = date,
                selectedDayTasks = tasksForDay
            )
        }
    }

    fun onTaskClick(task: Task) {
        _state.update {
            it.copy(
                activateBottomSheet = CalendarBottomSheetType.TaskDetails,
                selectedTask = task
            )
        }
    }

    fun onFilteredClick(petId: String) {
        _state.update { currentState ->
            val newSelection = currentState.selectedPetsIds.toMutableSet()
            if (newSelection.contains(petId)) {
                newSelection.remove(petId)
            } else {
                newSelection.add(petId)
            }
            calculateNewState(currentState.copy(selectedPetsIds = newSelection))
        }
    }

    fun onClearFilter() {
        _state.update {
            calculateNewState(it.copy(selectedPetsIds = emptySet()))
        }
    }

    fun onDismissBottomSheet() {
        _state.update {
            it.copy(
                activateBottomSheet = null,
                selectedTask = null,
                selectedDate = null,
                selectedDayTasks = emptyList()
            )
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

    fun onFilterClick() {
        _state.update {
            it.copy(
                activateBottomSheet = CalendarBottomSheetType.FilterPets
            )
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(error = null) }
    }

    fun onSuccessShown() {
        _state.update { it.copy(isRemoveSuccess = false) }
    }

    fun onDeleteConfirmed(task: Task, deleteWholeSeries: Boolean) {
        viewModelScope.launch {
            val previousList = _state.value.allUserTasks
            _state.update { currentState ->
                val updatedList = if (deleteWholeSeries && task.seriesId != null) {
                    currentState.allUserTasks.filter { it.seriesId != task.seriesId }
                } else {
                    currentState.allUserTasks.filter { it.id != task.id }
                }
                val newState =   currentState.copy(
                    allUserTasks = updatedList,
                    activateBottomSheet = if (currentState.selectedTask?.id == task.id) null else currentState.activateBottomSheet,
                    selectedTask = if (currentState.selectedTask?.id == task.id) null else currentState.selectedTask
                )
                calculateNewState(newState)
            }
            deleteTaskUseCase(task.id, deleteWholeSeries).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update { errorState ->
                            val revertedState = errorState.copy(
                                error = result.message ?: "Failed to delete task.",
                                allUserTasks = previousList,
                            )
                            calculateNewState(revertedState)
                        }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isRemoveSuccess = true) }
                    }
                    else -> Unit
                }
            }
        }
    }


    private fun updateTaskStatus(taskId: String, newStatus: taskStatusEnum) {
        viewModelScope.launch {
            val previousStatus = _state.value.allUserTasks.find {
                it.id == taskId
            }?.status
            _state.update { currentState ->
                val updatedList = currentState.allUserTasks.map {
                    if (it.id == taskId) {
                        it.copy(status = newStatus)
                    } else it
                }
                calculateNewState(currentState.copy(allUserTasks = updatedList))
            }
            changeTaskStatusUseCase(taskId, newStatus).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        if (previousStatus != null) {
                            _state.update { errorState ->
                                val revertedList = errorState.allUserTasks.map {
                                    if (it.id == taskId) it.copy(status = previousStatus)
                                    else it
                                }
                                val revertedState = errorState.copy(
                                    error = result.message ?: "Failed to update status",
                                    allUserTasks = revertedList
                                )
                                calculateNewState(revertedState)
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}