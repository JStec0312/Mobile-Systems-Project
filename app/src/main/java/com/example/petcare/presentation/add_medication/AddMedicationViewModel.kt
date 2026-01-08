package com.example.petcare.presentation.add_medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.add_medication.AddMedicationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddMedicationViewModel @Inject constructor(
    private val addMedicationUseCase: AddMedicationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddMedicationState())
    val state = _state.asStateFlow()

    fun onNameChange(newValue: String) { _state.update { it.copy(name = newValue) } }
    fun onFormChange(newForm: MedicationForm) { _state.update { it.copy(form = newForm) } }
    fun onDoseChange(newDose: String) { _state.update { it.copy(dose = newDose) } }
    fun onNotesChange(newNotes: String) { _state.update { it.copy(notes = newNotes) } }
    fun onStartDateChange(newDate: LocalDate) { _state.update { it.copy(startDate = newDate) } }
    fun onEndDateChange(newDate: LocalDate?) { _state.update { it.copy(endDate = newDate) } }
    fun onReminderTimeChange(newTime: LocalTime) { _state.update { it.copy(reminderTime = newTime) } }
    fun onReminderEnabledChange(isEnabled: Boolean) { _state.update { it.copy(isReminderEnabled = isEnabled) } }
    fun onRecurrenceToggled(isEnabled: Boolean) { _state.update { it.copy(isRecurring = isEnabled) } }
    fun onRecurrenceTypeChange(newType: MedRecurrenceType) { _state.update { it.copy(recurrenceType = newType) } }

    fun onIntervalChange(newInterval: String) {
        val interval = newInterval.filter { it.isDigit() }.toIntOrNull() ?: 1
        _state.update { it.copy(repeatInterval = interval) }
    }

    fun onDaySelected(day: DayOfWeek) {
        _state.update { currentState ->
            val currentDays = currentState.selectedDays.toMutableSet()
            if (currentDays.contains(day)) {
                currentDays.remove(day)
            } else {
                currentDays.add(day)
            }
            currentState.copy(selectedDays = currentDays)
        }
    }

    fun onSaveClick() {
        val currentState = _state.value

        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = "Medication name cannot be empty") }
            return
        }

        // Generowanie RRule dla backendu
        val rruleString = if (currentState.isRecurring) {
            buildRrule(currentState)
        } else {
            "" // Pusty string dla braku powtarzania
        }

        // Backend oczekuje List<LocalTime>. UI ma jedno, więc pakujemy w listę.
        val timesList = if (currentState.isReminderEnabled && currentState.reminderTime != null) {
            listOf(currentState.reminderTime)
        } else {
            emptyList()
        }

        // Backend UseCase wymaga daty 'to'. Jeśli user dał null (ongoing), dajemy +5 lat.
        val validEndDate = currentState.endDate ?: currentState.startDate.plus(5, DateTimeUnit.YEAR)

        viewModelScope.launch {
            addMedicationUseCase(
                name = currentState.name,
                form = currentState.form?.name,
                dose = currentState.dose,
                notes = currentState.notes,
                from = currentState.startDate,
                to = validEndDate,
                reccurenceString = rruleString,
                times = timesList
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, isSuccessful = true) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message ?: "An error occurred") }
                    }
                }
            }
        }
    }

    private fun buildRrule(state: AddMedicationState): String {
        val freq = when (state.recurrenceType) {
            MedRecurrenceType.DAILY -> "DAILY"
            MedRecurrenceType.WEEKLY -> "WEEKLY"
            MedRecurrenceType.MONTHLY -> "MONTHLY"
            MedRecurrenceType.AS_NEEDED -> "DAILY"
        }
        val sb = StringBuilder()
        sb.append("FREQ=$freq")

        if (state.repeatInterval > 1) {
            sb.append(";INTERVAL=${state.repeatInterval}")
        }

        if (state.recurrenceType == MedRecurrenceType.WEEKLY && state.selectedDays.isNotEmpty()) {
            val daysString = state.selectedDays
                .sorted()
                .joinToString(",") { day ->
                    day.name.take(2).uppercase()
                }
            sb.append(";BYDAY=$daysString")
        }
        return sb.toString()
    }

    fun onErrorShown() { _state.update { it.copy(error = null) } }
    fun onSuccessShown() { _state.update { it.copy(isSuccessful = false) } }
}