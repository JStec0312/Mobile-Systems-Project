package com.example.petcare.presentation.edit_medication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.edit_medication_use_case.EditMedicationUseCase
import com.example.petcare.domain.use_case.get_medication_by_id.GetMedicationByIdUseCase
import com.example.petcare.presentation.add_medication.MedRecurrenceType
import com.example.petcare.presentation.add_medication.MedicationForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import javax.inject.Inject

@HiltViewModel
class EditMedicationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val editMedicationUseCase: EditMedicationUseCase,
    private val getMedicationByIdUseCase: GetMedicationByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditMedicationState())
    val state = _state.asStateFlow()

    private val medicationId: String? = savedStateHandle.get<String>("medicationId")

    init {
        loadMedicationData()
    }

    private fun loadMedicationData() {
        if (medicationId == null) return

        viewModelScope.launch {
            getMedicationByIdUseCase(medicationId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        result.data?.let { med ->
                            val loadedForm = MedicationForm.values().find {
                                it.name.equals(med.form, ignoreCase = true)
                            }
                            val firstTime = med.times.firstOrNull()

                            val isRecurring = !med.reccurenceString.isNullOrBlank()

                            // 1. NAPRAWA ODCZYTU: Parsowanie typu powtarzalności
                            val recurrenceType = if (isRecurring) {
                                MedRecurrenceType.values().find {
                                    med.reccurenceString!!.contains(it.name, ignoreCase = true)
                                } ?: MedRecurrenceType.DAILY
                            } else {
                                MedRecurrenceType.DAILY
                            }

                            // 2. NAPRAWA ODCZYTU: Parsowanie interwału ze stringa (np. "FREQ=DAILY;INTERVAL=5")
                            var parsedInterval = "1"
                            if (isRecurring && med.reccurenceString != null) {
                                val parts = med.reccurenceString.split(";")
                                val intervalPart = parts.find { it.startsWith("INTERVAL=") }
                                if (intervalPart != null) {
                                    parsedInterval = intervalPart.substringAfter("=")
                                }
                            }

                            _state.update { currentState ->
                                currentState.copy(
                                    isLoading = false,
                                    medicationId = med.id,
                                    name = med.name,
                                    form = loadedForm,
                                    dose = med.dose ?: "",
                                    notes = med.notes ?: "",
                                    startDate = med.from,
                                    endDate = med.to,
                                    reminderTime = firstTime,
                                    isReminderEnabled = firstTime != null,
                                    isRecurring = isRecurring,
                                    recurrenceType = recurrenceType,
                                    repeatInterval = parsedInterval // Ustawiamy odczytaną wartość
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun onNameChange(newValue: String) { _state.update { it.copy(name = newValue) } }
    fun onFormChange(newForm: MedicationForm) { _state.update { it.copy(form = newForm) } }
    fun onDoseChange(newValue: String) { _state.update { it.copy(dose = newValue) } }
    fun onNotesChange(newValue: String) { _state.update { it.copy(notes = newValue) } }
    fun onStartDateChange(newDate: LocalDate) { _state.update { it.copy(startDate = newDate) } }
    fun onEndDateChange(newDate: LocalDate?) { _state.update { it.copy(endDate = newDate) } }

    fun onReminderTimeChange(newTime: LocalTime) { _state.update { it.copy(reminderTime = newTime) } }
    fun onReminderEnabledChange(isEnabled: Boolean) { _state.update { it.copy(isReminderEnabled = isEnabled) } }

    fun onRecurrenceToggled(isChecked: Boolean) { _state.update { it.copy(isRecurring = isChecked) } }
    fun onRecurrenceTypeChange(type: MedRecurrenceType) { _state.update { it.copy(recurrenceType = type) } }

    fun onIntervalChange(value: String) {
        val cleaned = value.filter { it.isDigit() }
        _state.update { it.copy(repeatInterval = cleaned) }
    }

    fun onDaySelected(day: DayOfWeek) {
        _state.update { currentState ->
            val currentDays = currentState.selectedDays.toMutableSet()
            if (currentDays.contains(day)) currentDays.remove(day) else currentDays.add(day)
            currentState.copy(selectedDays = currentDays)
        }
    }

    fun onSaveClick() {
        if (medicationId == null) return

        viewModelScope.launch {
            val currentState = _state.value

            val timesList = if(currentState.isReminderEnabled && currentState.reminderTime != null) {
                listOf(currentState.reminderTime)
            } else {
                emptyList()
            }

            // 3. NAPRAWA ZAPISU: Używamy funkcji buildRrule, żeby zawrzeć INTERVAL w stringu
            val recurrenceString = if (currentState.isRecurring) {
                buildRrule(currentState)
            } else {
                null
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
                reccurenceString = recurrenceString,
            ).collect { result ->
                when(result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, isSuccessful = true) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    // 4. NOWA FUNKCJA POMOCNICZA (Taka sama jak w AddMedicationViewModel)
    private fun buildRrule(state: EditMedicationState): String {
        val freq = when (state.recurrenceType) {
            MedRecurrenceType.DAILY -> "DAILY"
            MedRecurrenceType.WEEKLY -> "WEEKLY"
            MedRecurrenceType.MONTHLY -> "MONTHLY"
            MedRecurrenceType.AS_NEEDED -> "DAILY"
        }
        val sb = StringBuilder()
        sb.append("FREQ=$freq")

        val intervalInt = state.repeatInterval.toIntOrNull() ?: 1

        if (intervalInt > 1) {
            sb.append(";INTERVAL=$intervalInt")
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