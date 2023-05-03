package org.futo.circles.feature.photos.backup.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import org.futo.circles.R
import org.futo.circles.feature.photos.backup.MediaBackupDataSource
import org.futo.circles.feature.room.RoomAccountDataSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MediaBackupWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val mediaBackupDataSource: MediaBackupDataSource by inject()
    private val roomAccountDataSource: RoomAccountDataSource by inject()

    override suspend fun doWork(): Result {
        try {
            setForeground(createForegroundInfo())
            delay(10_000)
            backupMediaFiles()
        } catch (_: Exception) {
            return Result.failure()
        }
        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val id = MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID
        val cancelIntent =
            WorkManager.getInstance(applicationContext).createCancelPendingIntent(getId())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel()

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(applicationContext.getString(R.string.notification_media_backup))
            .setTicker(applicationContext.getString(R.string.notification_media_backup))
            .setContentText(applicationContext.getString(R.string.uploading))
            .setSmallIcon(R.drawable.ic_push_notification)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_close,
                applicationContext.getString(R.string.cancel),
                cancelIntent
            )
            .build()

        return ForegroundInfo(MEDIA_BACKUP_NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID,
                applicationContext.getString(R.string.notification_media_backup),
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = applicationContext.getString(R.string.notification_media_backup)
                setSound(null, null)
                setShowBadge(false)
            })
    }

    private suspend fun backupMediaFiles() {
        val backupSettings = roomAccountDataSource.getMediaBackupSettings()
        Log.d("MyLog", backupSettings.toString())
        if (backupSettings.shouldStartBackup(applicationContext))
            mediaBackupDataSource.startMediaBackup()
    }

    companion object {
        private const val MEDIA_BACKUP_NOTIFICATION_ID = 35
        private const val MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID =
            "MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID"
    }
}
