package com.example.petcare.presentation.walk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.use_case.end_walk.EndWalkUseCase
import com.example.petcare.domain.use_case.start_walk.StartWalkUseCase
import com.example.petcare.domain.use_case.walk.ObserveWalkPathUseCase
import com.example.petcare.presentation.service.LocationService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val startWalkUseCase: StartWalkUseCase,
    private val observeWalkPathUseCase: ObserveWalkPathUseCase,
    private val endWalkUseCase: EndWalkUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(WalkState())
    val state = _state.asStateFlow()

    private var timerJob: Job? = null
    private var currentWalkId: String? = null
    private var startTime: Instant? = null

    private var currentTotalDistanceMeters: Float = 0f
    private var processedPointsCount: Int = 0
    private var isReceiverRegistered: Boolean = false

    private val walkDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_WALK_UPDATED" -> {
                    val currentSteps = intent.getIntExtra("EXTRA_CURRENT_STEPS", 0)

                    viewModelScope.launch {
                        _state.update { it.copy(stepsValue = currentSteps.toString()) }
                    }
                }
                "ACTION_WALK_FINISHED_DATA" -> {
                    val finalSteps = intent.getIntExtra("EXTRA_STEPS", 0)
                    finalizeWalk(finalSteps)
                }
            }
        }
    }

    private fun registerReceiver() {
        if (isReceiverRegistered) return

        val filter = IntentFilter("ACTION_WALK_FINISHED_DATA")
        filter.addAction("ACTION_WALK_UPDATED")
        val listenToBroadcastsFromOtherApps = false
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            ContextCompat.RECEIVER_EXPORTED
        } else {
            ContextCompat.RECEIVER_NOT_EXPORTED
        }

        ContextCompat.registerReceiver(
            context,
            walkDataReceiver,
            filter,
            receiverFlags
        )
        isReceiverRegistered = true
    }

    override fun onCleared() {
        super.onCleared()
        try {
            if (isReceiverRegistered) {
                context.unregisterReceiver(walkDataReceiver)
                isReceiverRegistered = false
            }
        } catch (e: Exception) {
        }
    }

    fun onPermissionGranted() {
        registerReceiver()
        if (currentWalkId == null && !_state.value.isLoading) {
            startNewWalk()
        }
    }

    private fun startNewWalk() {
        viewModelScope.launch {
            startWalkUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val walkId = result.data?.id
                        if (walkId != null) {
                            currentWalkId = walkId
                            startTime = Clock.System.now()

                            currentTotalDistanceMeters = 0f
                            processedPointsCount = 0

                            startTimer()
                            startLocationService(walkId)
                            observeLocationUpdates(walkId)

                            _state.update { it.copy(isLoading = false) }
                        } else {
                            _state.update { it.copy(isLoading = false, error = "Failed to generate Walk ID") }
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message ?: "Unknown error occurred") }
                    }
                }
            }
        }
    }

    private fun startLocationService(walkId: String) {
        val intent = Intent(context, LocationService::class.java).apply {
            action = "ACTION_START"
            putExtra("EXTRA_WALK_ID", walkId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun observeLocationUpdates(walkId: String) {
        observeWalkPathUseCase(walkId)
            .onEach { allPoints ->
                val mapPoints = allPoints.map { LatLng(it.lat, it.lon) }

                updateDistanceEfficiently(allPoints)

                val distanceKmString = String.format("%.2f", currentTotalDistanceMeters / 1000)

                _state.update { currentState ->
                    currentState.copy(
                        routePoints = mapPoints,
                        distanceValue = distanceKmString
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateDistanceEfficiently(allPoints: List<WalkTrackPoint>) {
        if (allPoints.size < processedPointsCount) {
            processedPointsCount = 0
            currentTotalDistanceMeters = 0f
        }

        if (allPoints.size <= processedPointsCount) return

        val startIndex = if (processedPointsCount > 0) processedPointsCount else 1

        for (i in startIndex until allPoints.size) {
            val p1 = allPoints[i - 1]
            val p2 = allPoints[i]

            val results = FloatArray(1)
            Location.distanceBetween(
                p1.lat, p1.lon,
                p2.lat, p2.lon,
                results
            )
            currentTotalDistanceMeters += results[0]
        }

        processedPointsCount = allPoints.size
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

    fun onStopClick() {
        stopWalkService()
    }

    private fun stopWalkService() {
        timerJob?.cancel()
        val intent = Intent(context, LocationService::class.java).apply {
            action = "ACTION_STOP"
        }
        context.startService(intent)
    }
    private fun finalizeWalk(steps: Int) {
        val walkId = currentWalkId ?: return

        viewModelScope.launch {
            endWalkUseCase(
                walkId = walkId,
                totalDistanceMeters = currentTotalDistanceMeters,
                totalSteps = steps,
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false) }
                        currentWalkId = null
                        startTime = null
                        currentTotalDistanceMeters = 0f
                        processedPointsCount = 0
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message ?: "Unknown error occurred") }
                    }
                }
            }
        }
    }

    private fun formatSecondsToTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }
}