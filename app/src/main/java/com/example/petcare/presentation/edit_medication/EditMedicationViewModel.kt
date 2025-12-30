package com.example.petcare.presentation.edit_medication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.petcare.presentation.add_medication.MedRecurrenceType
import com.example.petcare.presentation.add_medication.MedicationForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditMedicationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(EditMedicationState())
    val state = _state.asStateFlow()

    private val medicationId: String? = savedStateHandle.get<String>("medicationId")

    init {
        if (medicationId != null) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            // Symulacja wczytania danych leku, który ma powtarzanie
            _state.update {
                it.copy(
                    medicationId = medicationId,
                    name = "Apap",
                    form = MedicationForm.TABLET,
                    dose = "1 tab",
                    notes = "Take with water",
                    startDate = today,

                    // Przykładowe dane powtarzania
                    isRecurring = true,
                    recurrenceType = MedRecurrenceType.DAILY,
                    repeatInterval = 1
                )
            }
        }
    }

    fun onNameChange(newValue: String) { _state.update { it.copy(name = newValue) } }
    fun onFormChange(newForm: MedicationForm) { _state.update { it.copy(form = newForm) } }
    fun onDoseChange(newValue: String) { _state.update { it.copy(dose = newValue) } }
    fun onNotesChange(newValue: String) { _state.update { it.copy(notes = newValue) } }
    fun onStartDateChange(newDate: LocalDate) { _state.update { it.copy(startDate = newDate) } }
    fun onEndDateChange(newDate: LocalDate?) { _state.update { it.copy(endDate = newDate) } }

    fun onRecurrenceToggled(isChecked: Boolean) {
        _state.update { it.copy(isRecurring = isChecked) }
    }

    fun onRecurrenceTypeChange(type: MedRecurrenceType) {
        _state.update { it.copy(recurrenceType = type) }
    }

    fun onIntervalChange(value: String) {
        val intValue = value.toIntOrNull() ?: 1
        _state.update { it.copy(repeatInterval = intValue) }
    }

    fun onDaySelected(day: DayOfWeek) {
        _state.update { currentState ->
            val currentDays = currentState.selectedDays.toMutableList()
            if (currentDays.contains(day)) {
                currentDays.remove(day)
            } else {
                currentDays.add(day)
            }
            currentState.copy(selectedDays = currentDays)
        }
    }

    fun onSaveClick() { _state.update { it.copy(isSuccessful = true) } }
    fun onErrorShown() { _state.update { it.copy(error = null) } }
    fun onSuccessShown() { _state.update { it.copy(isSuccessful = false) } }
}