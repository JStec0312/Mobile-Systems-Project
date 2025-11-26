package com.example.petcare.presentation.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.get_pet_by_id.GetPetByIdUseCase
import com.example.petcare.domain.use_case.get_tasks.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetDashboardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTasksUseCase: GetTasksUseCase,
    private val getPetByIdUseCase: GetPetByIdUseCase
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

    fun onTaskDone() {
        {/*TODO: implement on taskodn*/}
    }
}