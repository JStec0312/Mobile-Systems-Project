package com.example.petcare.presentation.walk

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.use_case.start_walk.StartWalkUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val startWalkUseCase: StartWalkUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(WalkState())
    val state = _state.asStateFlow()

    private var timerJob: Job? = null
    private var currentWalkId: String? = null

    private var startTime: Instant? = null
    private var totalDistanceMeters: Double = 0.0
    private var totalSteps: Int = 0

    private var lastLocation: Location? = null
    private val trackPoints = mutableListOf<WalkTrackPoint>()

    init {
        startNewWalk()
    }

    private fun startNewWalk() {
        viewModelScope.launch {
            startWalkUseCase().collect { result ->
                when(result) {
                    is Resource.Success -> {
                        currentWalkId = result.data?.id
                        startTime = Clock.System.now()
                        startTimer()
                        //startTrackingSteps()
                        //startTrackingLocation()
                        _state.update { it.copy(isLoading = false) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message ?: "Unknown error occurred") }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && startTime != null) {
                val now = Clock.System.now()
                val duration = now - startTime!!
                val seconds = duration.inWholeSeconds

                _state.update { it.copy(timerValue = formatSecondsToTime(seconds)) }

                delay(1.seconds)
            }
        }
    }

    private fun startTrackingLocation() {
    }

    private fun startTrackingSteps() {
    }

    private fun formatSecondsToTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }
}