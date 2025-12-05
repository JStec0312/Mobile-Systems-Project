package com.example.petcare.presentation.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.app.NotificationCompat
import com.example.petcare.domain.device_api.ILocationClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service(), SensorEventListener {
    @Inject
    lateinit var locationClient: ILocationClient
    @Inject
    lateinit var insertWalkTrackPointUseCase: com.example.petcare.domain.background_operations.InsertWalkTrackPointUseCase
    private  val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO) // Scope for coroutines blokada efektu domino przy bledach
    private lateinit var currentWalkId: String

    private var initialSteps = -1;
    private var currentSteps = 0;
    private var sensorManager: SensorManager? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    private fun updateNotification(steps: Int) {
        val notification = NotificationCompat.Builder(this, "walk_channel")
            .setContentTitle("Spacer trwa")
            .setContentText("Liczba kroków: $steps")
            .setSmallIcon(com.example.petcare.R.drawable.paw)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        val manager = getSystemService(android.app.NotificationManager::class.java)
        manager.notify(1, notification)
    }

    override fun onSensorChanged(event: SensorEvent){
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER){
            val stepsFromBoot = event.values[0].toInt()
            if (initialSteps<0){
                initialSteps = stepsFromBoot;
            }
            currentSteps = stepsFromBoot - initialSteps;
            updateNotification(currentSteps);
            val intent = Intent("ACTION_WALK_UPDATED")
            intent.putExtra("EXTRA_CURRENT_STEPS", currentSteps)
            intent.setPackage(packageName) // Upewnij się, że odbiornik jest w tej samej aplikacji
            sendBroadcast(intent)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            "ACTION_START" -> {
                val walkId = intent.getStringExtra("EXTRA_WALK_ID")
                if (walkId != null) {
                    currentWalkId = walkId
                    start()
                }

            }
            "ACTION_STOP" -> stop()
        }
        return START_STICKY // Service ma byc wznowiony jezeli system go zabije
    }
    private fun start() {
        val channelId = "walk_channel"
        val channelName = "Śledzenie spaceru"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                channelName,
                android.app.NotificationManager.IMPORTANCE_LOW // Low = bez dźwięku przy aktualizacji
            )
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Spacer trwa")
            .setContentText("Śledzę Twoją trasę...")
            .setSmallIcon(com.example.petcare.R.drawable.paw)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE) // Dla Android 12+
            .build()
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor != null){
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        try {
            startForeground(1, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        locationClient.getLocationUpdates(5000L)
            .catch { e -> e.printStackTrace() }
            .onEach { trackPoint ->
                val walkId = currentWalkId
                if (walkId != null) {
                    val pointWithId = trackPoint.copy(walkId = walkId)
                    insertWalkTrackPointUseCase(pointWithId)
                }
            }
            .launchIn(serviceScope)
    }
    private fun stop(){
        sensorManager?.unregisterListener(this);
        val intent = Intent("ACTION_WALK_FINISHED_DATA");
        intent.putExtra("EXTRA_STEPS", currentSteps);
        intent.setPackage(packageName)
        sendBroadcast(intent);
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    override fun onBind(intent: Intent?) = null

}