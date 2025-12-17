package com.example.petcare.presentation.calendar

import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.model.Task
import kotlinx.datetime.LocalDate
import java.time.YearMonth

data class CalendarState(
    val currentMonth: YearMonth = YearMonth.now(),
    val allUserTasks: List<Task> = emptyList(),
    val allPets: List<Pet> = emptyList(),
    val selectedPetsIds: Set<String> = emptySet(),
    val tasksByDate: Map<LocalDate, List<Task>> = emptyMap(),
    val activateBottomSheet: CalendarBottomSheetType? = null,
    val selectedTask: Task? = null,
    val selectedDate: LocalDate? = null,
    val selectedDayTasks: List<Task> = emptyList(),

    val isRemoveSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class CalendarBottomSheetType {
    data object DayDetails: CalendarBottomSheetType()
    data object TaskDetails: CalendarBottomSheetType()
    data object FilterPets: CalendarBottomSheetType()
}

