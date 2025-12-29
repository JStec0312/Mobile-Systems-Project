package com.example.petcare.presentation.medication_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.use_case.list_medications.ListMedicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicationDetailsViewModel @Inject constructor(
    private val listMedicationsUseCase: ListMedicationsUseCase,
    private val petProvider: IPetProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(MedicationDetailsState())
    val state = _state.asStateFlow()

    // Pobieramy ID przekazane w nawigacji
    private val medicationId: String? = savedStateHandle.get<String>("medicationId")

    init {
        loadMedication()
    }

    private fun loadMedication() {
        if (medicationId == null) {
            _state.update { it.copy(error = "Invalid Medication ID") }
            return
        }

        viewModelScope.launch {
            val petId = petProvider.getCurrentPetId()
            if (petId != null) {
                // Tutaj normalnie byłby useCase: getMedicationById(medicationId)
                // Używamy listMedicationsUseCase i filtrujemy (tymczasowo)
                listMedicationsUseCase(petId).collect { result ->
                    when (result) {
                        is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                        is Resource.Success -> {
                            val med = result.data?.find { it.id == medicationId }
                            if (med != null) {
                                _state.update { it.copy(isLoading = false, medication = med) }
                            } else {
                                _state.update { it.copy(isLoading = false, error = "Medication not found") }
                            }
                        }
                        is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }
}