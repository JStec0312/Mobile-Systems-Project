package com.example.petcare.presentation.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.use_case.list_medications.ListMedicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

sealed class MedicationUiEvent {
    data class ShareReport(val reportContent: String) : MedicationUiEvent()
}

@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val listMedicationsUseCase: ListMedicationsUseCase,
    private val petProvider: IPetProvider
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

    fun onExportClick() {
        val meds = _state.value.medications
        if (meds.isEmpty()) return

        viewModelScope.launch {
            val report = generateCsvReport(meds)
            _uiEvent.send(MedicationUiEvent.ShareReport(report))
        }
    }

    // Prosta generacja CSV
    private fun generateCsvReport(meds: List<Medication>): String {
        val sb = StringBuilder()
        sb.append("Name,Form,Dose,Status,Start Date,End Date,Notes\n")

        meds.forEach { med ->
            val cleanNotes = med.notes?.replace(",", " ") ?: ""
            val status = if (med.active) "Active" else "Completed"

            sb.append("${med.name},")
            sb.append("${med.form ?: ""},")
            sb.append("${med.dose ?: ""},")
            sb.append("$status,")
            sb.append("${med.from},")
            sb.append("${med.to ?: "Ongoing"},")
            sb.append("$cleanNotes\n")
        }
        return sb.toString()
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
        return list.sortedWith(compareBy({ it.dayLabel == "Tomorrow" }, { it.time }))
            .take(5)
    }
}