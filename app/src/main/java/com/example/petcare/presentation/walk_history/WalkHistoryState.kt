package com.example.petcare.presentation.walk_history

import com.example.petcare.domain.model.Walk

data class WalkHistoryState(
    val walks: List<Walk> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)