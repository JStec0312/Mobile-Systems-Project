package com.example.petcare.presentation.task_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.get_task_by_id.GetTaskByIdUseCase
import dagger.hilt.android.lifecycle.ActivityRetainedSavedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(TaskDetailsState())
    val state = _state.asStateFlow()

    init {
        val taskId = savedStateHandle.get<String>("taskId")
        if(taskId != null) {
            loadTask(taskId)
        } else {
            _state.update { it.copy(error = "Task ID not found") }
        }
    }

    private fun loadTask(taskId: String) {
        viewModelScope.launch {
            getTaskByIdUseCase(taskId).collect { result ->
                when(result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, task = result.data) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message ?: "Error") }
                    }
                }
            }
        }
    }
}