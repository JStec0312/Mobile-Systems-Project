package com.example.petcare.presentation.walk_stats

import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.model.Walk
import kotlinx.datetime.LocalDate

data class WalkStatsState (
    val pet: Pet? = null,
    val walks: List<Walk> = emptyList(),

    val selectedDate: LocalDate,
    val isMonthly: Boolean = false,

    val isLoading: Boolean = false,
    val error: String? = null
) {
    val totalDistance: Double = walks.sumOf { (it.distanceMeters ?: 0f).toDouble() } / 1000.0
    val avgDistance: Double = if (walks.isNotEmpty()) totalDistance / walks.size else 0.0
    val maxDistance: Double = walks.maxOfOrNull { (it.distanceMeters ?: 0f).toDouble() / 1000.0 } ?: 0.0

    val totalSteps: Int = walks.sumOf { it.steps ?: 0 }
    val avgSteps: Int = if (walks.isNotEmpty()) totalSteps / walks.size else 0
    val maxSteps: Int = walks.maxOfOrNull { it.steps ?: 0 } ?: 0

    val totalDuration: Long = walks.sumOf { (it.durationSec ?: 0).toLong() } / 60
    val avgDuration: Long = if (walks.isNotEmpty()) totalDuration / walks.size else 0
    val maxDuration: Long = walks.maxOfOrNull { (it.durationSec ?: 0).toLong() / 60 } ?: 0
}