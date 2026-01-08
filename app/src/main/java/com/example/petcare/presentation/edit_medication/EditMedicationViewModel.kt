package com.example.petcare.presentation.edit_medication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
<<<<<<< Updated upstream
=======
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.edit_medication_use_case.EditMedicationUseCase
>>>>>>> Stashed changes
import com.example.petcare.presentation.add_medication.MedRecurrenceType
import com.example.petcare.presentation.add_medication.MedicationForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
<<<<<<< Updated upstream
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
=======
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
>>>>>>> Stashed changes
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditMedicationViewModel @Inject constructor(
<<<<<<< Updated upstream
    savedStateHandle: SavedStateHandle
=======
    savedStateHandle: SavedStateHandle,
    private val editMedicationUseCase: EditMedicationUseCase
>>>>>>> Stashed changes
) : ViewModel() {

    private val _state = MutableStateFlow(EditMedicationState())
    val state = _state.asStateFlow()

    private val medicationId: String? = savedStateHandle.get<String>("medicationId")

    init {
<<<<<<< Updated upstream
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
=======
        loadMedicationData()
    }

    private fun loadMedicationData() {
        if (medicationId != null) {
            // Symulacja wczytania danych (bo brak GetMedicationById w UI)
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            _state.update {
                it.copy(
                    medicationId = medicationId,
                    name = "Simulated Med",
                    form = MedicationForm.TABLET,
                    dose = "1 pill",
                    notes = "",
                    startDate = today,
                    // Ustawiamy czas (symulacja)
                    reminderTime = LocalTime(8, 0),
                    isReminderEnabled = true,
                    isRecurring = true
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    fun onRecurrenceToggled(isChecked: Boolean) {
        _state.update { it.copy(isRecurring = isChecked) }
    }

    fun onRecurrenceTypeChange(type: MedRecurrenceType) {
        _state.update { it.copy(recurrenceType = type) }
    }

    fun onIntervalChange(value: String) {
        val intValue = value.toIntOrNull() ?: 1
=======
    // --- NAPRAWA: Poprawne nazwy funkcji czasu ---
    fun onReminderTimeChange(newTime: LocalTime) { _state.update { it.copy(reminderTime = newTime) } }
    fun onReminderEnabledChange(isEnabled: Boolean) { _state.update { it.copy(isReminderEnabled = isEnabled) } }
    // ---------------------------------------------

    fun onRecurrenceToggled(isChecked: Boolean) { _state.update { it.copy(isRecurring = isChecked) } }
    fun onRecurrenceTypeChange(type: MedRecurrenceType) { _state.update { it.copy(recurrenceType = type) } }

    fun onIntervalChange(value: String) {
        val intValue = value.filter { it.isDigit() }.toIntOrNull() ?: 1
>>>>>>> Stashed changes
        _state.update { it.copy(repeatInterval = intValue) }
    }

    fun onDaySelected(day: DayOfWeek) {
        _state.update { currentState ->
<<<<<<< Updated upstream
            val currentDays = currentState.selectedDays.toMutableList()
            if (currentDays.contains(day)) {
                currentDays.remove(day)
            } else {
                currentDays.add(day)
            }
=======
            val currentDays = currentState.selectedDays.toMutableSet()
            if (currentDays.contains(day)) currentDays.remove(day) else currentDays.add(day)
>>>>>>> Stashed changes
            currentState.copy(selectedDays = currentDays)
        }
    }

<<<<<<< Updated upstream
    fun onSaveClick() { _state.update { it.copy(isSuccessful = true) } }
=======
    fun onSaveClick() {
        if (medicationId == null) return

        viewModelScope.launch {
            val currentState = _state.value

            // Backend wymaga List<LocalTime>
            val timesList = if(currentState.isReminderEnabled && currentState.reminderTime != null) {
                listOf(currentState.reminderTime)
            } else {
                emptyList()
            }

            editMedicationUseCase(
                medicationId = medicationId,
                newName = currentState.name,
                newForm = currentState.form?.name,
                newDose = currentState.dose,
                newNotes = currentState.notes,
                newFrom = currentState.startDate,
                newTo = currentState.endDate,
                newTimes = timesList,
                reccurenceString = null
            ).collect { result ->
                when(result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, isSuccessful = true) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

>>>>>>> Stashed changes
    fun onErrorShown() { _state.update { it.copy(error = null) } }
    fun onSuccessShown() { _state.update { it.copy(isSuccessful = false) } }
}