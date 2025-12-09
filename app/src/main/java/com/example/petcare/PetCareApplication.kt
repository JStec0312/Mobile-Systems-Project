package com.example.petcare

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.petcare.config.Settings
import com.example.petcare.data.worker.UpcomingNotificationsWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class PetCareApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        scheduleUpcomingNotificationsWorker()
    }

    private fun scheduleUpcomingNotificationsWorker() {
        val request = PeriodicWorkRequestBuilder<UpcomingNotificationsWorker>(
            Settings.PERIODIC_WORKER_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "upcoming_notifications",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        WorkManager.getInstance(this).enqueue(request)
    }
}
