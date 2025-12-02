package com.example.petcare.presentation.walk
import android.app.NotificationChannel
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.petcare.domain.device_api.IWalkTracker
import com.example.petcare.domain.use_case.process_location_update.ProcessLocationUpdateUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class WalkService  : LifecycleService() {
    @Inject
    lateinit var walkTracker: IWalkTracker

    @Inject
    lateinit var processLocationUpdateUseCas: ProcessLocationUpdateUseCase
    private var serviceJob: Job? = null
    private var currentWalkId: String? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_WALK_ID = "EXTRA_WALK_ID"

        private const val NOTIFICATION_CHANNEL_ID = "walk_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.d("WalkService: onStartCommand called with action=${intent?.action}")

        when (intent?.action) {
            ACTION_START -> {
                val walkId = intent.getStringExtra(EXTRA_WALK_ID)
                Timber.d("WalkService: ACTION_START received with walkId=$walkId")
                if (walkId != null) {
                    currentWalkId = walkId
                    startForegroundService()
                    startTracking()
                }
            }

            ACTION_STOP -> {
                Timber.d("WalkService: ACTION_STOP received")
                stopTracking()
                stopSelf() // Zabija serwis i powiadomienie
            }
        }
        return START_STICKY
    }
    private fun startTracking() {
        Timber.d("WalkService: Starting location tracking...")
        serviceJob?.cancel()
        serviceJob = lifecycleScope.launch {
            try {
                walkTracker.getLocationUpdates(intervalMillis = 3000L)
                    .collect { location ->
                        try {
                            Timber.d("WalkService: Received location - lat=${location.latitude}, lon=${location.longitude}")
                            currentWalkId?.let { walkId ->
                                Timber.d("WalkService: Processing location update for walkId=$walkId")
                                processLocationUpdateUseCas(
                                    walkId,
                                    location.latitude,
                                    location.longitude
                                )
                                Timber.d("WalkService: Location processed successfully")
                            }
                        } catch (e: Exception) {
                            Timber.e("WalkService: Error processing location: ${e.message}")
                            e.printStackTrace()
                        }
                    }
            } catch (e: Exception) {
                Timber.e("WalkService: Error collecting locations: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun stopTracking() {
        serviceJob?.cancel()
        currentWalkId == null;
    }

    private fun startForegroundService() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Walk in Progress")
            .setContentText("Your walk is being tracked.")
            .setSmallIcon(com.example.petcare.R.drawable.paw)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Walk Tracking",
                android.app.NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}


