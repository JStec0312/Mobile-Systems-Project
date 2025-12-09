package com.example.petcare.presentation.edit_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.domain.use_case.edit_task.EditTaskUseCase
import com.example.petcare.domain.use_case.get_task_by_id.GetTaskByIdUseCase
import com.example.petcare.presentation.edit_pet.EditPetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val editTaskUseCase: EditTaskUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(EditTaskState())
    val state = _state.asStateFlow()
    private var petId: String? = null

    init {
        val taskId = savedStateHandle.get<String>("taskId")
        this.petId = savedStateHandle.get<String>("petId")
        if(taskId != null) {
            loadTaskData(taskId)
        } else {
            _state.update { it.copy(error = "Invalid task ID") }
        }
    }

    private fun loadTaskData(id: String) {
        viewModelScope.launch {
            getTaskByIdUseCase(id).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        val task = result.data
                        if (task != null) {
                            val dateTime = task.date.toLocalDateTime(TimeZone.currentSystemDefault())
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    taskId = task.id,
                                    seriesId = task.seriesId,
                                    title = task.title,
                                    type = task.type,
                                    notes = task.notes ?: "",
                                    selectedDate = dateTime.date,
                                    selectedTime = dateTime.time
                                )
                            }
                        } else {
                            _state.update { it.copy(isLoading = false, error = "Task not found") }
                        }
                    }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _state.update { it.copy(title = newTitle) }
    }

    fun onTypeChange(newType: taskTypeEnum) {
        _state.update { it.copy(type = newType) }
    }

    fun onNotesChange(newNotes: String) {
        _state.update { it.copy(notes = newNotes) }
    }

    fun onTimeChange(newTime: LocalTime) {
        _state.update { it.copy(selectedTime = newTime) }
    }

    fun onSaveClick() {
        val currentState = _state.value

        if(currentState.title.isBlank()) {
            _state.update { it.copy(error = "Title cannot be empty") }
            return
        }
        _state.update { it.copy(showConfirmDialog = true) }
    }

    fun onConfirmSave(editWholeSeries: Boolean) {
        _state.update { it.copy(showConfirmDialog = false) }
        performSave(editWholeSeries)
    }

    fun onDismissDialog() {
        _state.update { it.copy(showConfirmDialog = false) }
    }

    private fun performSave(editWholeSeries: Boolean) {
        val currentState = _state.value
        val safeDate = currentState.selectedDate ?: return
        val safeTime = currentState.selectedTime ?: return

        val newDateTime = LocalDateTime(safeDate, safeTime)
        val newInstant = newDateTime.toInstant(TimeZone.currentSystemDefault())
        val currentPetId = petId
        if (currentPetId == null) {
            _state.update { it.copy(error = "Pet ID is missing. Cannot save.") }
            return
        }
        viewModelScope.launch {
            editTaskUseCase(
                taskId = currentState.taskId,
                petId = currentPetId,
                seriesId = currentState.seriesId,
                title = currentState.title,
                type = currentState.type,
                notes = currentState.notes,
                date = newInstant,
                priority = taskPriorityEnum.normal,
                editWholeSeries = editWholeSeries
            ).collect { result ->
                when (result) {
                    is Resource.Success -> _state.update {
                        it.copy(
                            isSuccessful = true,
                            isSaving = false
                        )
                    }

                    is Resource.Error -> _state.update {
                        it.copy(
                            isSaving = false,
                            error = result.message
                        )
                    }

                    is Resource.Loading -> _state.update { it.copy(isSaving = true) }
                }
            }
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(error = null) }
    }

    fun onSuccessShown() {
        _state.update { it.copy(isSuccessful = false) }
    }
}