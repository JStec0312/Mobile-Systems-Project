package com.example.petcare.service.walk_tracking

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.petcare.R // Upewnij się, że ten import pasuje do Twojego pakietu R!
import com.example.petcare.config.Settings
import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.repository.IWalkRepository
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import com.example.petcare.service.interfaces.ILocationTracker
import com.example.petcare.exceptions.PermissionFailure.LocationPermissionDenied
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class WalkTrackingService : Service() {

    @Inject lateinit var locationTracker: ILocationTracker
    @Inject lateinit var walkRepository: IWalkRepository
    @Inject lateinit var walkTrackPointRepository: IWalkTrackPointRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var startTime: Long = 0L
    private var lastLocation: WalkTrackPoint? = null
    private var totalDistanceMeters: Double = 0.0
    private lateinit var currentWalkId: String;

    companion object {
        const val NOTIFICATION_ID = 123
        const val CHANNEL_ID = "walk_tracking_channel"
        const val ACTION_START = "START_WALK_TRACKING"
        const val ACTION_STOP = "STOP_WALK_TRACKING"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val walkId = intent.getStringExtra("WALK_ID")
                if (walkId != null) {
                    currentWalkId = walkId
                    startForegroundService()
                    startTracking()
                }
            }
            ACTION_STOP -> {
                stopTracking()
            }
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel tracking",
                NotificationManager.IMPORTANCE_LOW // LOW = brak dźwięku przy każdej aktualizacji
            ).apply {
                description = "Showing a status of an active walk"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = buildNotification("Walk started", "Waiting for gps signal")

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startTracking() {
        startTime = System.currentTimeMillis()

        locationTracker.getLocationFlow(intervalMs = Settings.WALK_TRACK_POINT_INTERVAL_MS, walkId = currentWalkId) // np. co 3 sekundy

            .catch { e ->
                e.printStackTrace()
            }
            .onEach { newPoint ->
                processLocationUpdate(newPoint)
            }
            .launchIn(serviceScope)
    }

    private suspend fun processLocationUpdate(newPoint: WalkTrackPoint) {
        if (lastLocation != null) {
            val distance = calculateDistance(lastLocation!!, newPoint)
            totalDistanceMeters += distance
        }
        lastLocation = newPoint

        val durationSeconds = (System.currentTimeMillis() - startTime) / 1000

        currentWalkId?.let { id ->
            walkTrackPointRepository.addWalkTrackPoint(newPoint)
        }

        updateNotification(totalDistanceMeters, durationSeconds)
    }

    private fun stopTracking() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        serviceScope.cancel()
    }

    private fun updateNotification(distance: Double, durationSec: Long) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val timeStr = String.format("%02d:%02d", durationSec / 60, durationSec % 60)
        val distStr = String.format("%.2f km", distance / 1000)

        val notification = buildNotification("Walk pending: $timeStr", "Distance: $distStr")
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(title: String, content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Upewnij się, że masz tę ikonę lub zmień na własną
            .setOnlyAlertOnce(true) // Nie wibruje przy każdej aktualizacji
            .setOngoing(true) // Użytkownik nie może usunąć powiadomienia
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun calculateDistance(p1: WalkTrackPoint, p2: WalkTrackPoint): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            p1.lat,p1.lon,
            p2.lat, p2.lon,
            results
        )
        return results[0].toDouble()
    }

    override fun onBind(p0: Intent?): IBinder? = null
}