package com.example.petcare.data.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.petcare.R
import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.config.Settings
import com.example.petcare.domain.model.UpcomingItem
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.INotificationRepository
import com.example.petcare.domain.use_case.get_upcoming_items.GetUpcomingItemsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import timber.log.Timber
import javax.inject.Inject
import kotlin.concurrent.timer
import kotlin.time.Duration.Companion.minutes

@HiltWorker
class UpcomingNotificationsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
     private val getUpcomingItems: GetUpcomingItemsUseCase,
    private val settingsRepository: INotificationRepository,
    private val userProvider: IUserProvider,
): CoroutineWorker(appContext, params) {

    companion object {
        private const val ADVANCE_MINUTES: Int = Settings.NOTIFICATION_BEFORE_MINUTES     // ile przed
        private const val WINDOW_MINUTES: Int = Settings.NOTIFICATION_WINDOW_MINUTES       // szerokosc okna
    }

    override suspend fun doWork(): Result {
        Timber.d("UpcomingNotificationsWorker started")

        val userId = userProvider.getUserId()
        if (userId == null) {
            Timber.d("No user logged in, skipping notifications")
            return Result.success()
        }


        val settings = settingsRepository.getForUser(userId)
        val tasksEnabled = settings.any { it.category == notificationCategoryEnum.tasks && it.enabled }
        val medsEnabled  = settings.any { it.category == notificationCategoryEnum.meds && it.enabled }
        Timber.d("Notification settings - tasks: $tasksEnabled, meds: $medsEnabled")
        if (!tasksEnabled && !medsEnabled) {
            return Result.success()
        }

        val now = Clock.System.now()
        val from = now + ADVANCE_MINUTES.minutes
        val to   = from + WINDOW_MINUTES.minutes


        val items = getUpcomingItems(from, to)
        Timber.d("Upcoming items in the next $WINDOW_MINUTES minutes (after $ADVANCE_MINUTES minutes): ${items.map { it.title }}")
        val filtered = items.filter {
            when (it.type) {
                notificationCategoryEnum.tasks -> tasksEnabled
                notificationCategoryEnum.meds   -> medsEnabled
            }
        }

        filtered.forEach { item ->
            showNotification(item)
        }
        Timber.d("UpcomingNotificationsWorker finished, ${filtered.size} notifications shown")
        Timber.d("List of notified items: ${filtered.map { it.title }}")

        return Result.success()
    }

    @SuppressLint("ServiceCast")
    private fun showNotification(item: UpcomingItem) {
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = when (item.type) {
            notificationCategoryEnum.tasks -> "tasks_channel"
            notificationCategoryEnum.meds  -> "meds_channel"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = when (item.type) {
                notificationCategoryEnum.tasks -> "Pet's tasks"
                notificationCategoryEnum.meds  -> "Pet's medications"
            }
            val channel = NotificationChannel(
                channelId,
                name,
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val title =  when (item.type) {
            notificationCategoryEnum.tasks -> "Upcoming task"
            notificationCategoryEnum.meds  -> "Upcoming medication"
        }


        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.paw)
            .setContentTitle(title)
            .setContentText(item.title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(item.id.hashCode(), notification)
    }
}
