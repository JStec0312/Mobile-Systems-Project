package com.example.petcare.presentation.walk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare.common.Resource
import com.example.petcare.domain.use_case.end_walk.EndWalkUseCase
import com.example.petcare.domain.use_case.observe_new_points.ObserveNewPointsUseCase
import com.example.petcare.domain.use_case.start_walk.StartWalkUseCase
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
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val startWalkUseCase: StartWalkUseCase,
    private val endWalkUseCase: EndWalkUseCase,
    private val observeNewPointsUseCase: ObserveNewPointsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(WalkState())
    val state = _state.asStateFlow()

    // Lokalna lista (bufor), żeby nie pobierać historii z flow przy każdym update
    private val _localRoutePoints = mutableListOf<LatLng>()

    private var timerJob: Job? = null
    private var currentWalkId: String? = null
    private var startTime: Instant? = null

    // Liczniki numeryczne (do obliczeń), stan trzyma Stringi
    private var totalDistanceMeters: Double = 0.0
    private var totalSteps: Int = 0

    init {
        startNewWalk()
    }

    private fun startNewWalk() {
        viewModelScope.launch {
            startWalkUseCase().collect { result ->
                when(result) {
                    is Resource.Success -> {
                        val walk = result.data
                        if (walk != null) {
                            Timber.d("WalkViewModel: Walk started successfully - walkId=${walk.id}")
                            currentWalkId = walk.id
                            startTime = Clock.System.now()


                            // 1. Start UI (Timer)
                            startTimer()

                            // 2. Start Backend (Service GPS + Nasłuchiwanie Bazy)
                            startBackgroundService(walk.id)
                            startObservingNewPoints(walk.id)

                            _state.update { it.copy(isLoading = false) } // isLocationEnabled można tu ustawić na true
                        }
                    }
                    is Resource.Error -> {
                        Timber.e("WalkViewModel: Error starting walk - ${result.message}")
                        _state.update {
                            it.copy(isLoading = false, error = result.message ?: "Błąd rozpoczęcia spaceru")
                        }
                    }
                    is Resource.Loading -> {
                        Timber.d("WalkViewModel: Loading walk...")
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }

    private fun startObservingNewPoints(walkId: String) {
        // Nasłuchujemy "gorącego" strumienia nowych punktów z FakeRepo/Room
        Timber.d("WalkViewModel: Starting to observe new points for walkId=$walkId")
        observeNewPointsUseCase(walkId)
            .onEach { newPoint ->
                Timber.d("WalkViewModel: Received new point - lat=${newPoint.lat}, lon=${newPoint.lon}")
                val newLatLng = LatLng(newPoint.lat, newPoint.lon)

                // A. Obliczamy przyrost dystansu
                if (_localRoutePoints.isNotEmpty()) {
                    val lastLatLng = _localRoutePoints.last()
                    val dist = calculateDistance(lastLatLng, newLatLng)
                    totalDistanceMeters += dist
                }

                // B. Dodajemy do lokalnej listy
                _localRoutePoints.add(newLatLng)

                // C. Aktualizujemy UI (formatowanie Stringów zgodnie z Twoim State)
                _state.update {
                    it.copy(
                        routePoints = _localRoutePoints.toList(), // Kopia listy dla Compose
                        distanceValue = "%.2f km".format(totalDistanceMeters / 1000),
                        currentLocation = newLatLng
                        // stepsValue = "$totalSteps" // Jeśli punkt zawiera kroki, zaktualizuj też to
                    )
                }
                Timber.d("WalkViewModel: Updated state - currentLocation=${newLatLng}")
            }
            .launchIn(viewModelScope)
    }

    fun onFinishWalkClick() {
        val walkId = currentWalkId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Zatrzymaj wszystko
            timerJob?.cancel()
            stopBackgroundService()


            _state.update { it.copy(isLoading = false) }
            // Tu obsłuż nawigację "Go Back" w UI (np. przez Channel lub Event)
        }
    }

    // --- Helpery ---

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && startTime != null) {
                val now = Clock.System.now()
                val duration = now - startTime!!
                val seconds = duration.inWholeSeconds

                _state.update {
                    it.copy(timerValue = formatSecondsToTime(seconds))
                }
                delay(1.seconds)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatSecondsToTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }

    private fun calculateDistance(p1: LatLng, p2: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
        return results[0].toDouble()
    }

    private fun startBackgroundService(walkId: String) {
        val intent = Intent(context, WalkService::class.java).apply {
            action = WalkService.ACTION_START
            putExtra(WalkService.EXTRA_WALK_ID, walkId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun stopBackgroundService() {
        val intent = Intent(context, WalkService::class.java).apply {
            action = WalkService.ACTION_STOP
        }
        context.startService(intent)
    }
}