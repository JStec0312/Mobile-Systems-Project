package com.example.petcare.presentation.medication

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.use_case.delete_medication.DeleteMedicationUseCase
import com.example.petcare.domain.use_case.list_medications.ListMedicationsUseCase
import com.example.petcare.domain.use_case.med_history_to_pdf.MedHistoryToPdfUseCase // <--- IMPORT
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext // <--- IMPORT
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import javax.inject.Inject

sealed class MedicationUiEvent {
    // ZMIANA: Zamiast Stringa (CSV), przesyłamy Plik (PDF)
    data class SharePdf(val file: File) : MedicationUiEvent()
    data class ShowMessage(val message: String) : MedicationUiEvent()
}

@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val listMedicationsUseCase: ListMedicationsUseCase,
    private val deleteMedicationUseCase: DeleteMedicationUseCase,
    private val medHistoryToPdfUseCase: MedHistoryToPdfUseCase, // <--- WSTRZYKUJEMY PDF USECASE
    private val petProvider: IPetProvider,
    @ApplicationContext private val context: Context // <--- POTRZEBNE DO ZAPISU PLIKU
) : ViewModel() {

    private val _state = MutableStateFlow(MedicationState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<MedicationUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadMedications()
    }

    fun loadMedications() {
        viewModelScope.launch {
            val petId = petProvider.getCurrentPetId()
            if (petId != null) {
                listMedicationsUseCase(petId).collect { result ->
                    when (result) {
                        is Resource.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                        is Resource.Success -> {
                            val meds = result.data ?: emptyList()
                            val upcoming = calculateUpcomingDoses(meds)
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    medications = meds,
                                    upcomingDoses = upcoming
                                )
                            }
                        }
                        is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            } else {
                _state.update { it.copy(error = "No pet selected") }
            }
        }
    }

    fun deleteMedication(medicationId: String) {
        viewModelScope.launch {
            val previousList = _state.value.medications

            _state.update { currentState ->
                val updatedList = currentState.medications.filter { it.id != medicationId }
                currentState.copy(
                    medications = updatedList,
                    upcomingDoses = calculateUpcomingDoses(updatedList)
                )
            }

            deleteMedicationUseCase(medicationId).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        _uiEvent.send(MedicationUiEvent.ShowMessage("Medication removed successfully"))
                    }
                    is Resource.Error -> {
                        _state.update { currentState ->
                            currentState.copy(
                                medications = previousList,
                                upcomingDoses = calculateUpcomingDoses(previousList)
                            )
                        }
                        _uiEvent.send(MedicationUiEvent.ShowMessage(result.message ?: "Failed to delete medication"))
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    // --- LOGIKA EKSPORTU PDF ---
    fun onExportClick() {
        val meds = _state.value.medications
        if (meds.isEmpty()) {
            viewModelScope.launch {
                _uiEvent.send(MedicationUiEvent.ShowMessage("No medications to export"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Przekazujemy null, null aby pobrać całą historię (bez filtrowania dat)
            medHistoryToPdfUseCase(from = null, to = null).collect { result ->
                when(result) {
                    is Resource.Loading -> {
                        // Już obsłużone wyżej
                    }
                    is Resource.Success -> {
                        val pdfBytes = result.data
                        if (pdfBytes != null) {
                            try {
                                // 1. Tworzymy plik w cache aplikacji
                                val fileName = "MedicationHistory_${Clock.System.now().toEpochMilliseconds()}.pdf"
                                val file = File(context.cacheDir, fileName)

                                // 2. Zapisujemy bajty
                                file.writeBytes(pdfBytes)

                                // 3. Wysyłamy zdarzenie do UI
                                _state.update { it.copy(isLoading = false) }
                                _uiEvent.send(MedicationUiEvent.SharePdf(file))

                            } catch (e: Exception) {
                                _state.update { it.copy(isLoading = false) }
                                _uiEvent.send(MedicationUiEvent.ShowMessage("Failed to save PDF: ${e.message}"))
                            }
                        } else {
                            _state.update { it.copy(isLoading = false) }
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(MedicationUiEvent.ShowMessage(result.message ?: "Failed to generate PDF"))
                    }
                }
            }
        }
    }

    private fun calculateUpcomingDoses(meds: List<Medication>): List<UpcomingDoseUiModel> {
        val list = mutableListOf<UpcomingDoseUiModel>()
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentTime = now.time

        meds.filter { it.active }.forEach { med ->
            med.times.forEach { time ->
                if (time > currentTime) {
                    list.add(UpcomingDoseUiModel(med.name, time, "Today"))
                }
                list.add(UpcomingDoseUiModel(med.name, time, "Tomorrow"))
            }
        }
        return list.sortedWith(compareBy({ it.dayLabel == "Tomorrow" }, { it.time })).take(5)
    }
}