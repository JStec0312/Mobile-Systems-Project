package com.example.petcare.presentation.add_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.domain.use_case.add_task.AddTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AddTaskState())
    val state = _state.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _state.update { it.copy(title = newTitle) }
    }

    fun onTypeChange(newType: taskTypeEnum) {
        _state.update { it.copy(type = newType) }
    }

    fun onDateChange(newDate: LocalDate) {
        _state.update { it.copy(selectedDate = newDate) }
    }

    fun onTimeChange(newTime: LocalTime) {
        _state.update { it.copy(selectedTime = newTime) }
    }

    fun onNotesChange(newNotes: String) {
        _state.update { it.copy(notes = newNotes) }
    }
    fun onRecurrenceToggled(isEnabled: Boolean) {
        _state.update { it.copy(isRecurring = isEnabled) }
    }

    fun onRecurrenceTypeChange(newRecurrenceType: RecurrenceType) {
        _state.update { it.copy(recurrenceType = newRecurrenceType) }
    }

    fun onIntervalChange(newInterval: String) {
        val interval = newInterval.filter { it.isDigit() }.toIntOrNull() ?: 0
        if(interval > 0) {
            _state.update { it.copy(repeatInterval = interval) }
        }
        else if(newInterval.isEmpty()) {
            _state.update { it.copy(repeatInterval = 0) }
        }
     }

    fun onDaySelected(day: DayOfWeek) {
        _state.update { currentState ->
            val currentDays = currentState.selectedDaysOfWeek.toMutableSet()
            if(currentDays.contains(day)) {
                currentDays.remove(day)
            } else {
                currentDays.add(day)
            }
            currentState.copy(selectedDaysOfWeek = currentDays)
        }
    }

    fun onSaveClick() {
        val currentState = _state.value

        if(currentState.title.isBlank()) {
            _state.update { it.copy(error = "Title cannot be empty") }
            return
        }
        if(currentState.type == null) {
            _state.update { it.copy(error = "Please select a task type") }
            return
        }
        if(currentState.isRecurring && currentState.repeatInterval < 1) {
            _state.update { it.copy(error = "Repeat interval must be at least 1") }
            return
        }
        if(currentState.selectedDate == null) {
            _state.update { it.copy(error = "Please select a date") }
            return
        }
        if (currentState.selectedTime == null) {
            _state.update { it.copy(error = "Please select a time") }
            return
        }

        val startDateTime = LocalDateTime(currentState.selectedDate, currentState.selectedTime)
        val startInstant = startDateTime.toInstant(TimeZone.currentSystemDefault())

        val rruleString = if(currentState.isRecurring) {
            buildRrule(currentState)
        } else {
            null
        }
        viewModelScope.launch {
            addTaskUseCase(
                type = currentState.type,
                title = currentState.title,
                notes = currentState.notes,
                priority = taskPriorityEnum.normal,
                date = startInstant,
                rrule = rruleString
            ).collect { result ->
                when(result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, isSuccessful = true) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message ?: "Error") }
                    }
                }
            }
        }
    }

    fun buildRrule(state: AddTaskState): String {
        val freq = when(state.recurrenceType) {
            RecurrenceType.DAILY -> "DAILY"
            RecurrenceType.WEEKLY -> "WEEKLY"
            RecurrenceType.MONTHLY -> "MONTHLY"
        }
        val sb = StringBuilder()
        sb.append("FREQ=$freq")

        if(state.repeatInterval > 1) {
            sb.append(";INTERVAL=${state.repeatInterval}")
        }

        if(state.recurrenceType == RecurrenceType.WEEKLY) {
            val daysString = state.selectedDaysOfWeek
                .sorted()
                .joinToString(",") { day ->
                    day.name.take(2).uppercase()
                }
            sb.append(";BYDAY=$daysString")
        }
        return sb.toString()
    }

    fun onErrorShown() {
        _state.update { it.copy(error = null) }
    }

    fun onSuccessShown() {
        _state.update { it.copy(isSuccessful = false) }
    }
}