package com.example.petcare.presentation.all_tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.use_case.get_tasks.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AllTasksViewModel @Inject constructor(
    private  val getTasksUseCase: GetTasksUseCase
) : ViewModel() {
    private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private val _state = MutableStateFlow(AllTasksState(selectedDate = today))
    val state = _state.asStateFlow()

    init {
        updateDateText(today)
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            getTasksUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val allTasks = result.data ?: emptyList()
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                allTasksCache = allTasks,
                                tasksForSelectedDate = filterTasksByDate(
                                    allTasks,
                                    currentState.selectedDate ?: today
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun onNextDayClick() {
        changeDate(1)
    }
    fun onPrevDayClick() {
        changeDate(-1)
    }

    private fun changeDate(daysToAdd: Int) {
        _state.update { currentState ->
            val newDate = currentState.selectedDate?.plus(daysToAdd, DateTimeUnit.DAY)
                ?: return@update currentState
            updateDateText(newDate)
            currentState.copy(
                selectedDate = newDate,
                tasksForSelectedDate = filterTasksByDate(
                    currentState.allTasksCache,
                    newDate
                )
            )
        }
    }

    private fun filterTasksByDate(allTasks: List<Task>, date: LocalDate): List<Task> {
        return allTasks.filter { task ->
            val taskDate = task.date.toLocalDateTime(TimeZone.currentSystemDefault()).date
            taskDate == date
        }.sortedBy { it.date }
    }

    private fun updateDateText(date: LocalDate) {
        val month = date.month.name
        val day = date.dayOfMonth
        val year = date.year
        val suffix = getDayOfMonthSuffix(day)

        val formattedDate = "$month $day$suffix, $year"

        val dayOfWeek = date.dayOfWeek.name.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        _state.update {
            it.copy(
                dateText = formattedDate,
                dayOfWeek = dayOfWeek
            )
        }
    }

    private fun getDayOfMonthSuffix(n: Int): String {
        if(n in 11..13) return "TH"
        return when(n % 10) {
            1 -> "ST"
            2 -> "ND"
            3 -> "RD"
            else -> "TH"
        }
    }


    fun onTaskDone(task: Task) {
        // TODO: updateTaskUseCase
    }
}