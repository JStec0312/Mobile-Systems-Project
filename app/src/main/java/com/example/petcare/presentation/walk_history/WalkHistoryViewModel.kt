package com.example.petcare.presentation.walk_history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.get_walks_in_date_range.GetWalksInDateRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalkHistoryViewModel @Inject constructor(
    private val getWalksUseCase: GetWalksInDateRangeUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val petId: String = checkNotNull(savedStateHandle["petId"])
    private val _state = MutableStateFlow(WalkHistoryState())
    val state = _state.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            getWalksUseCase(petId).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        _state.update { it.copy(walks = result.data?.sortedByDescending { it.startedAt } ?: emptyList(), isLoading = false) }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(error = result.message, isLoading = false)
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
}