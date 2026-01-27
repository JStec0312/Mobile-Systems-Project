package com.example.petcare.presentation.walk_stats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.get_pet_by_id.GetPetByIdUseCase
import com.example.petcare.domain.use_case.get_walks_in_date_range.GetWalksInDateRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class WalkStatsViewModel @Inject constructor(
    private val getWalksInDateRangeUseCase: GetWalksInDateRangeUseCase,
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val petId: String = checkNotNull(savedStateHandle["petId"])

    private val _state = MutableStateFlow(
        WalkStatsState(selectedDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    )
    val state = _state.asStateFlow()

    init {
        loadPet()
        loadWalks()
    }

    private fun loadPet() {
        viewModelScope.launch {
            getPetByIdUseCase(petId).collect { result ->
                if (result is Resource.Success) {
                    _state.update { it.copy(pet = result.data) }
                }
                if (result is Resource.Error) {
                    _state.update { it.copy(error = result.message) }
                }
            }
        }
    }

    fun loadWalks() {
        val (from, to) = calculateRange(_state.value.selectedDate, _state.value.isMonthly)
        viewModelScope.launch {
            getWalksInDateRangeUseCase(petId, from, to).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                walks = result.data ?: emptyList(),
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun toggleViewMode() {
        _state.update { it.copy(isMonthly = !it.isMonthly) }
        loadWalks()
    }

    fun navigateTime(forward: Boolean) {
        _state.update { currentState ->
            val delta = if(forward) 1L else -1L
            val nextDate = if(currentState.isMonthly) {
                val javaDate = java.time.LocalDate.of(currentState.selectedDate.year, currentState.selectedDate.monthNumber, 1)
                val updated = javaDate.plusMonths(delta)
                kotlinx.datetime.LocalDate(updated.year, updated.monthValue, 1)
            } else {
                currentState.selectedDate.plus(delta * 7, DateTimeUnit.DAY)
            }
            currentState.copy(selectedDate = nextDate)
        }
        loadWalks()
    }

    private fun calculateRange(date: kotlinx.datetime.LocalDate, isMonthly: Boolean): Pair<kotlinx.datetime.LocalDate, kotlinx.datetime.LocalDate> {
        val javaDate = java.time.LocalDate.of(date.year, date.monthNumber, date.dayOfMonth)
        return if(isMonthly) {
            val start = javaDate.with(TemporalAdjusters.firstDayOfMonth())
            val end = javaDate.with(TemporalAdjusters.lastDayOfMonth())
            Pair(
                kotlinx.datetime.LocalDate(start.year, start.monthValue, start.dayOfMonth),
                kotlinx.datetime.LocalDate(end.year, end.monthValue, end.dayOfMonth)
            )
        } else {
            val start = javaDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val end = start.plusDays(6)
            Pair(
                kotlinx.datetime.LocalDate(start.year, start.monthValue, start.dayOfMonth),
                kotlinx.datetime.LocalDate(end.year, end.monthValue, end.dayOfMonth)
            )
        }
    }
}