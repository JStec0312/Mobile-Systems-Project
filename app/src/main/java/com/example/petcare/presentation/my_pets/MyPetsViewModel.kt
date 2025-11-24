package com.example.petcare.presentation.my_pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.use_case.get_pets.GetPetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPetsViewModel @Inject constructor(
    private val getPetsUseCase: GetPetsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MyPetsState())
    val state: StateFlow<MyPetsState> = _state.asStateFlow()

    private var originalPetsList: List<Pet> = emptyList()
    private var searchJob: Job? = null

    init {
        getPets()
    }
    private fun getPets() {

        getPetsUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                    Timber.d("getPets: ${result}" )

                }
                is Resource.Success -> {
                val pets = result.data ?: emptyList()
                    originalPetsList = pets
                    _state.update { it.copy(pets = pets, isLoading = false, error = null) }
                    Timber.d("getPets: ${result}" )
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message ?: "An unexpected error occurred", isLoading = false) }
                    Timber.d("getPets: ${result}" )

                }
            }
        }.launchIn(viewModelScope)

    }

    fun onSearchQueryChange(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            if (query.isBlank()) {
                _state.update { it.copy(pets = originalPetsList) }
            } else {
                val filtered = originalPetsList.filter { pet ->
                    pet.name.contains(query, ignoreCase = true) ||
                            (pet.breed?.contains(query, ignoreCase = true) ==true)
                }
                _state.update { it.copy(pets = filtered) }
            }
        }
    }
}