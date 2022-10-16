package com.defendend.weather.foregroundwork

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.defendend.weather.R
import kotlinx.coroutines.delay

class ForegroundWorker constructor(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {

            val foregroundInfo = createForegroundInfo()
            setForeground(foregroundInfo)

            while (true) {
                delay(60_000*60)
            }

            Result.success()
        } catch (ex: Throwable) {
            Result.failure()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        // Use a different id for each Notification.
        val notificationId = 1
        return ForegroundInfo(notificationId, createNotification())
    }

    private fun createNotification(): Notification {
        // This PendingIntent can be used to cancel the Worker.
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        val builder = NotificationCompat.Builder(this.applicationContext, CHANNEL_ID)
            .setContentTitle("Weather")
            .setTicker("ddd")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(android.R.drawable.ic_delete, "Cancel Download", intent)

        createChannel(builder, CHANNEL_ID)

        return builder.build()
    }

    private fun createChannel(
        notificationBuilder: NotificationCompat.Builder,
        id: String
    ) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager

        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        val channel = NotificationChannel(
            id,
            "Weather",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "WeatherApp Notifications"
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "Weather_Channel"
    }
}